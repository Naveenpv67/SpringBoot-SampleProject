By shifting this logic directly into `FlexcubeOriginatorService`, we eliminate manual decryption boilerplate in your caller classes and consolidate your payment profile lookup into a single, cohesive, transaction-scoped component.

To achieve this, we will inject the **`CustIdCacheService`** (which we designed in the previous step) to cleanly resolve the decrypted plain Customer ID.

Here is the updated implementation of `FlexcubeOriginatorService` including the nested `CustomerAccountDetails` record and the new unified helper method.

---

### Updated `FlexcubeOriginatorService`

```java
package com.payment.api.service;

import com.payment.api.cache.DataOrchestrator;
import com.payment.api.dto.FcOrgInfoCacheDTO;
import com.payment.api.dto.PaymentRequest;
import com.payment.api.dto.FlexcubeOrgInfoRequestOBPDTO;
import com.payment.api.response.CustomerOriginatorInfoFCResponse;
import com.payment.api.mapper.FcOrgInfoCacheMapper;
import com.payment.api.config.ObpFcConfig;
import com.payment.api.client.OBPDownstreamCaller;
import com.payment.api.dao.AccountDetailsDAO;
import com.payment.api.dto.AccountDtlsCache;
import com.payment.api.algorithm.AESGCMEncDecAlgorithm;
import com.payment.api.exception.CustomException;
import com.payment.api.enums.IssuerResponseEnum;
import com.payment.api.enums.ErrorKind;
import com.payment.api.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ValidationException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlexcubeOriginatorService {

    private final DataOrchestrator<FcOrgInfoCacheDTO> fcOrgInfoCacheManager;
    private final ObpFcConfig obpFcConfig;
    private final OBPDownstreamCaller obpDownstreamCaller;
    private final FcOrgInfoCacheMapper mapper;
    private final AESGCMEncDecAlgorithm aesGCMEncDecAlgorithm;
    private final AccountDetailsDAO accountDetailsDAO;
    private final CustIdCacheService custIdCacheService; // Injected our highly optimized Customer ID cache service

    /**
     * Public Record representing consolidated customer and account details.
     */
    public record CustomerAccountDetails(
            String customerId, 
            String accountNumber, 
            String mobileNumber, 
            String emailId, 
            String accountType, 
            String customerFullName
    ) {}

    /**
     * Unified Orchestrator Method.
     * Combines the originator details API/cache flow, Customer ID decryption flow, 
     * and dynamic account attributes to construct the plain CustomerAccountDetails DTO.
     */
    public CustomerAccountDetails getPlainCustomerAndAccountDetails(PaymentRequest request) {
        log.info("Starting unified resolution of plain customer and account details for hash: {}", request.getHashAccNo());

        // 1. Get the cached/OBP originator information
        CustomerOriginatorInfoFCResponse fcResponse = getOriginatorInfo(request);

        // 2. Resolve the plain decrypted customer ID (utilizing our CustIdCacheService)
        String plainCustId = custIdCacheService.getPlainCustomerId(request.getHashCustomerId(), request.getClientId());

        // 3. Retrieve dynamic attributes (accountType, customerFullName) from the account details cache
        String cachePK = PaymentCachePkUtil.generatePk(request.getReferenceId(), request.getHashCustomerId(), request.getClientChannel());
        AccountDtlsCache accountDtlsCache = accountDetailsDAO.getAccountDetails(cachePK);

        if (accountDtlsCache == null || accountDtlsCache.getCipherAccDtls() == null) {
            String errorMsg = String.format("Account details not found for cache PK: %s", cachePK);
            log.error(errorMsg);
            throw new CustomException(
                    IssuerResponseEnum.DATABASE_RECORD_NOT_FOUND.getErrorCodes(),
                    "User profile not found.",
                    errorMsg,
                    ErrorKind.ERR_KIND_DATABASE
            );
        }

        Optional<AccountDtlsCache.CipherAccountDtls> cipherAccountOpt = accountDtlsCache.getCipherAccDtls().stream()
                .filter(accDetails -> accDetails != null && accDetails.getHashAccNo().equals(request.getHashAccNo()))
                .findFirst();

        if (cipherAccountOpt.isEmpty()) {
            String errorMsg = String.format("Account details not found for hash: %s inside PK: %s", request.getHashAccNo(), cachePK);
            log.error(errorMsg);
            throw new CustomException(
                    IssuerResponseEnum.DATABASE_RECORD_NOT_FOUND.getErrorCodes(),
                    "Account details not found.",
                    errorMsg,
                    ErrorKind.ERR_KIND_DATABASE
            );
        }

        AccountDtlsCache.CipherAccountDtls cipherAccount = cipherAccountOpt.get();

        // 4. Return consolidated, plain-text details
        return new CustomerAccountDetails(
                plainCustId,
                fcResponse.getAccNo(),
                fcResponse.getMobileNo(),
                fcResponse.getEmailId(),
                cipherAccount.getAccType(),
                cipherAccount.getCustFullName()
        );
    }

    /**
     * Retrieves the Originator Information.
     * Checks Aerospike cache first; falls back to external OBP API call and caches result on miss.
     */
    public CustomerOriginatorInfoFCResponse getOriginatorInfo(PaymentRequest paymentRequest) {
        String accNoHash = paymentRequest.getHashAccNo();

        // 1. Check Aerospike Cache
        try {
            FcOrgInfoCacheDTO cachedDto = fcOrgInfoCacheManager.find(accNoHash);
            if (cachedDto != null) {
                log.info("Cache hit in Aerospike for account key: {}", accNoHash);
                
                CustomerOriginatorInfoFCResponse response = mapper.toFcResponseFromCacheDto(cachedDto);
                enrichResponseWithDecryptedData(response, cachedDto);
                
                return response;
            }
        } catch (Exception ex) {
            log.error("Cache read failed for key: {}. Proceeding with request flow.", accNoHash, ex);
        }

        // 2. Resolve missing plain account number if necessary
        log.info("Cache miss. Processing Flexcube OBP API call preparation for key (Account No hash): {}", accNoHash);

        if (paymentRequest.getPlainAccNo() == null || paymentRequest.getPlainAccNo().trim().isEmpty()) {
            log.info("Plain account number is missing in PaymentRequest. Resolving from cipher cache for key: {}", accNoHash);

            String plainAccNo = getPlainAccNoFromHashAccNo(
                    paymentRequest.getClientChannel(),
                    paymentRequest.getReferenceId(),
                    paymentRequest.getHashCustomerId(),
                    accNoHash
            );
            paymentRequest.setPlainAccNo(plainAccNo);
        }

        // 3. Call external OBP service
        CustomerOriginatorInfoFCResponse apiResponse =
                callFlexcubeApiToFetchOriginatorInfo(paymentRequest);

        // 4. Cache the API response on success
        if (apiResponse != null) {
            try {
                if (!StringUtils.hasText(apiResponse.getAccNo())) {
                    apiResponse.setAccNo(paymentRequest.getPlainAccNo());
                }

                FcOrgInfoCacheDTO dtoToCache = mapper.toCacheDtoFromFcResponse(apiResponse, accNoHash);
                enrichCacheDtoWithSecureData(dtoToCache, apiResponse, paymentRequest.getPlainAccNo());

                fcOrgInfoCacheManager.save(dtoToCache);
                log.info("Successfully cached originator info in Aerospike for key: {}", accNoHash);
            } catch (Exception ex) {
                log.error("Failed to write updated originator info to cache for key: {}", accNoHash, ex);
            }
        }

        return apiResponse;
    }

    private void enrichResponseWithDecryptedData(CustomerOriginatorInfoFCResponse response, FcOrgInfoCacheDTO cachedDto) {
        if (StringUtils.hasText(cachedDto.getAccNoEnc())) {
            String decAccNo = aesGCMEncDecAlgorithm.decrypt(cachedDto.getAccNoEnc());
            if (decAccNo != null) {
                response.setAccNo(decAccNo.replaceAll("\"", ""));
            }
        }

        if (StringUtils.hasText(cachedDto.getMobEnc())) {
            String decMob = aesGCMEncDecAlgorithm.decrypt(cachedDto.getMobEnc());
            if (decMob != null) {
                response.setMobileNo(decMob.replaceAll("\"", ""));
            }
        }

        if (StringUtils.hasText(cachedDto.getEmailEnc())) {
            String decEmail = aesGCMEncDecAlgorithm.decrypt(cachedDto.getEmailEnc());
            if (decEmail != null) {
                response.setEmailId(decEmail.replaceAll("\"", ""));
            }
        }
        
        response.setAccNoHash(cachedDto.getAccNoHash());
    }

    private void enrichCacheDtoWithSecureData(FcOrgInfoCacheDTO dtoToCache, CustomerOriginatorInfoFCResponse response, String plainAccNo) {
        String rawMobile = response.getMobileNo();
        if (StringUtils.hasText(rawMobile)) {
            rawMobile = rawMobile.replaceAll("\"", "");
            if (rawMobile.length() == 10) {
                rawMobile = "91" + rawMobile;
            }
        }

        String rawEmail = response.getEmailId();

        if (StringUtils.hasText(plainAccNo)) {
            dtoToCache.setAccNoEnc(aesGCMEncDecAlgorithm.encrypt(plainAccNo));
            dtoToCache.setAccNoHash(dtoToCache.getAccNoHash());
        }

        if (StringUtils.hasText(rawMobile)) {
            dtoToCache.setMobEnc(aesGCMEncDecAlgorithm.encrypt(rawMobile));
            dtoToCache.setMobHash(HashingUtil.generateSHA256Hash(rawMobile));
            dtoToCache.setMobMask(MaskingUtil.maskDataFromFrontAndBack.apply(rawMobile, 3, 3));
        }

        if (StringUtils.hasText(rawEmail)) {
            dtoToCache.setEmailEnc(aesGCMEncDecAlgorithm.encrypt(rawEmail));
            dtoToCache.setEmailHash(HashingUtil.generateSHA256Hash(rawEmail));
        }
    }

    private CustomerOriginatorInfoFCResponse callFlexcubeApiToFetchOriginatorInfo(PaymentRequest paymentRequest) {
        try {
            FlexcubeOrgInfoRequestOBPDTO transformedRequest = FlexcubeOrgInfoRequestTransformer
                    .transformOrgInfoRequest(paymentRequest.getPlainAccNo(), obpFcConfig);
            log.info("Calling OBP/FC To Fetch Originator Info with request: {}", CommonUtils.validateForLogForging(
                    SensitiveDataMasker.maskSensitiveJson(CommonUtils.convertObjectToJsonString(transformedRequest))));
            CustomerOriginatorInfoFCResponse customerOriginatorInfoFCResponse = obpDownstreamCaller
                    .callOBPToFetchOriginatorInfo(transformedRequest, paymentRequest);
            log.info("Received response from OBP/FC for Fetch Originator Info: {}",
                    CommonUtils.validateForLogForging(SensitiveDataMasker.maskSensitiveJson(
                            CommonUtils.convertObjectToJsonString(customerOriginatorInfoFCResponse))));
            return customerOriginatorInfoFCResponse;
        } catch (Exception e) {
            log.error("Error during Fetching Originator Info transaction", e);
            throw new CustomException(
                    IssuerResponseEnum.INTERNAL_SERVER_ERROR.getErrorCodes(),
                    "Failed to process transaction while Fetching Originator Info",
                    "An error occurred while fetching originator information: " + ExceptionUtils.getMessage(e),
                    ErrorKind.ERR_KIND_INTERNAL
            );
        }
    }

    public String getPlainAccNoFromHashAccNo(String clientChannel, String referenceId,
                                             String hashCustomerId, String hashAccNo) {

        String accountNumber = null;
        String cachePK = PaymentCachePkUtil.generatePk(referenceId, hashCustomerId, clientChannel);

        AccountDtlsCache accountDtlsCache = accountDetailsDAO.getAccountDetails(cachePK);

        if (accountDtlsCache == null || accountDtlsCache.getCipherAccDtls() == null) {
            throw new ValidationException("Invalid Account number for the customer");
        }
        Optional<AccountDtlsCache.CipherAccountDtls> optionalVal = accountDtlsCache.getCipherAccDtls().stream()
                .filter(accDetails -> accDetails != null && accDetails.getHashAccNo().equals(hashAccNo))
                .findAny();

        if (optionalVal.isEmpty()) {
            throw new ValidationException("Invalid Account number for the customer");
        } else {
            accountNumber = aesGCMEncDecAlgorithm.decrypt(optionalVal.get().getEncrAccNo());
            accountNumber = accountNumber.replaceAll("\"", "");
        }
        return accountNumber;
    }
}
```
