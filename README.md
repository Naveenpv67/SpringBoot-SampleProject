package com.payment.api.service;

import com.payment.api.cache.CustIdCacheManager;
import com.payment.api.dto.CustIdCacheDTO;
import com.payment.api.dto.FetchCustIdRequestDTO;
import com.payment.api.dto.FetchCustIdRespDTO;
import com.payment.api.dto.CommonServicesResponseDTO;
import com.payment.api.client.CsUserStatusClient;
import com.payment.api.config.CsConfig;
import com.payment.api.algorithm.AESGCMEncDecAlgorithm;
import com.payment.api.exception.CustomException;
import com.payment.api.enums.IssuerResponseEnum;
import com.payment.api.enums.ErrorKind;
import com.payment.api.util.CommonUtils;
import com.payment.api.util.SensitiveDataMasker;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.esapi.ESAPI;
import org.slf4j.MDC;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustIdCacheService {

    // Using your static virtual thread executor definition
    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private final CustIdCacheManager cacheManager;
    private final AESGCMEncDecAlgorithm aesGCMEncDecAlgorithm;
    private final CsUserStatusClient csUserStatusClient;
    private final CsConfig csConfig;

    /**
     * Retrieves the plain Customer ID.
     * Uses synchronous execution for the blocking HTTP downstream call but offloads 
     * caching and slide touch operations asynchronously via the runAsyncWithMDC helper.
     */
    public String getPlainCustomerId(String hashCustomerId, String clientId) {
        log.info("Resolving plain Customer ID for hash key: {} and clientId: {}", hashCustomerId, clientId);

        // 1. Synchronous read check (Async TTL extension on hit)
        String plainCustomerId = getPlainCustomerIdAndTouchAsync(hashCustomerId);
        if (StringUtils.hasText(plainCustomerId)) {
            log.info("Cache hit in Aerospike for Customer ID key: {}", hashCustomerId);
            return plainCustomerId;
        }

        // 2. Cache Miss: Fetch from downstream user status service (blocking network I/O)
        log.info("Cache miss for Customer ID key: {}. Querying downstream client...", hashCustomerId);
        plainCustomerId = fetchPlainCustIdFromDownstream(hashCustomerId, clientId);

        // 3. Async Cache Population (Using your established MDC helper)
        if (StringUtils.hasText(plainCustomerId)) {
            final String finalPlainCustomerId = plainCustomerId;
            
            runAsyncWithMDC(
                () -> writeToCacheIfAbsent(finalPlainCustomerId, hashCustomerId),
                MDC.getCopyOfContextMap(),
                VIRTUAL_THREAD_EXECUTOR
            );
        }

        return plainCustomerId;
    }

    /**
     * Asynchronously pre-populates the cache.
     * Checks mapping existence and resolves missing values entirely in the background.
     */
    public void cacheCustomerId(String hashCustomerId, String clientId) {
        log.info("Offloading Customer ID caching process to virtual threads for key: {}", hashCustomerId);
        
        runAsyncWithMDC(() -> {
            // Read check in background
            CustIdCacheDTO existing = cacheManager.find(hashCustomerId);
            if (existing != null) {
                log.info("Mapping already exists in cache for key: {}. Skipping write.", hashCustomerId);
                touchCacheRecord(hashCustomerId, existing);
                return;
            }

            // Fetch in background
            String plainCustomerId = fetchPlainCustIdFromDownstream(hashCustomerId, clientId);
            if (StringUtils.hasText(plainCustomerId)) {
                writeToCache(plainCustomerId, hashCustomerId);
            }
        }, MDC.getCopyOfContextMap(), VIRTUAL_THREAD_EXECUTOR);
    }

    /**
     * Reads plain Customer ID and schedules an asynchronous TTL extension (Touch).
     */
    private String getPlainCustomerIdAndTouchAsync(String hashCustomerId) {
        try {
            CustIdCacheDTO cachedDto = cacheManager.find(hashCustomerId);
            if (cachedDto != null && cachedDto.getCustIdEnc() != null) {
                // Async TTL extension so the main execution thread is unblocked
                touchCacheRecord(hashCustomerId, cachedDto);

                String decryptedVal = aesGCMEncDecAlgorithm.decrypt(cachedDto.getCustIdEnc());
                if (decryptedVal != null) {
                    return decryptedVal.replaceAll("\"", "");
                }
            }
        } catch (Exception ex) {
            log.error("Failed to read or touch Customer ID key: {}", hashCustomerId, ex);
        }
        return null;
    }

    /**
     * Verifies existence before writing. Designed for background execution.
     */
    private void writeToCacheIfAbsent(String plainCustomerId, String hashCustomerId) {
        try {
            CustIdCacheDTO existing = cacheManager.find(hashCustomerId);
            if (existing == null) {
                writeToCache(plainCustomerId, hashCustomerId);
            } else {
                log.info("Mapping already established for Customer ID key: {}. Skipping write.", hashCustomerId);
            }
        } catch (Exception ex) {
            log.error("Error performing write-if-absent verification for key: {}", hashCustomerId, ex);
        }
    }

    /**
     * Writes directly to Aerospike. Designed for background execution.
     */
    private void writeToCache(String plainCustomerId, String hashCustomerId) {
        try {
            CustIdCacheDTO dto = CustIdCacheDTO.builder()
                .pk(hashCustomerId)
                .custIdHash(hashCustomerId)
                .custIdEnc(aesGCMEncDecAlgorithm.encrypt(plainCustomerId))
                .build();

            cacheManager.save(dto);
            log.info("Successfully populated Customer ID mapping in Aerospike for key: {}", hashCustomerId);
        } catch (Exception ex) {
            log.error("Failed to write Customer ID mapping to cache for key: {}", hashCustomerId, ex);
        }
    }

    /**
     * Schedules the renewal of a cache record's expiration asynchronously.
     */
    private void touchCacheRecord(String hashCustomerId, CustIdCacheDTO cachedDto) {
        runAsyncWithMDC(() -> {
            try {
                cacheManager.save(cachedDto); 
                log.debug("TTL renewed asynchronously for key: {}", hashCustomerId);
            } catch (Exception ex) {
                log.warn("Could not renew TTL asynchronously for Customer ID key: {}", hashCustomerId, ex);
            }
        }, MDC.getCopyOfContextMap(), VIRTUAL_THREAD_EXECUTOR);
    }

    /**
     * Your standardized MDC-aware Asynchronous Helper Method.
     */
    private CompletableFuture<Void> runAsyncWithMDC(Runnable runnable, Map<String, String> context, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            try {
                MDC.setContextMap(context);
                runnable.run();
            } finally {
                MDC.clear();
            }
        }, executor);
    }

    /**
     * Downstream Client Integration (Synchronous Network Call).
     */
    private String fetchPlainCustIdFromDownstream(String hashCustId, String clientId) {
        try {
            FetchCustIdRequestDTO requestDTO = new FetchCustIdRequestDTO();
            requestDTO.setRequestId(CommonUtils.getRandomValue());
            requestDTO.setChannelId(csConfig.getApiEndpoints().getUserStatusService().getChannelId());
            requestDTO.setPlatform(csConfig.getApiEndpoints().getUserStatusService().getPlatform());
            requestDTO.setChannel(csConfig.getApiEndpoints().getUserStatusService().getChannel(clientId));
            requestDTO.setQueryId(csConfig.getApiEndpoints().getUserStatusService().getQueryId());
            requestDTO.setQueryValue(hashCustId);

            CommonServicesResponseDTO<FetchCustIdRespDTO> response = csUserStatusClient.fetchPlainCustId(requestDTO);

            if (response == null) {
                String errorMessage = "User status service returned a null response.";
                log.error(ESAPI.encoder().encodeForHTML(errorMessage));
                throw new CustomException(
                        IssuerResponseEnum.THIRD_PARTY_INVALID_RESPONSE.getErrorCodes(),
                        "Failed to retrieve user status.",
                        errorMessage,
                        ErrorKind.ERR_KIND_CS
                );
            }

            if ("200".equals(response.getStatusCode())) {
                return processUserStatusResponse(response);
            } else {
                String errorMessage = String.format(
                        "Failed call to user status service. HTTP Status: %s, Message: %s",
                        response.getStatusCode(), response.getMessage());
                log.error(ESAPI.encoder().encodeForHTML(errorMessage));
                throw new CustomException(
                        IssuerResponseEnum.THIRD_PARTY_SERVICE_ERROR.getErrorCodes(),
                        "Failed to retrieve user status.",
                        errorMessage,
                        ErrorKind.ERR_KIND_CS
                );
            }
        } catch (FeignException e) {
            log.error("Feign exception occurred while calling fetchPlainCustId: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in fetchPlainCustIdFromDownstream: {}", e.getMessage(), e);
            throw new CustomException(
                    IssuerResponseEnum.THIRD_PARTY_SERVICE_ERROR.getErrorCodes(),
                    "Failed to retrieve user status.",
                    "An unexpected error occurred while calling user status service. Nested exception: " + ExceptionUtils.getMessage(e),
                    ErrorKind.ERR_KIND_CS
            );
        }
    }

    /**
     * Decodes and validates downstream API response payload.
     */
    private String processUserStatusResponse(CommonServicesResponseDTO<FetchCustIdRespDTO> responseDTO) {
        log.info("Received response from user status service: {}",
                ESAPI.encoder().encodeForHTML(SensitiveDataMasker.maskSensitiveJson(CommonUtils.convertObjectToJsonString(responseDTO))));

        if (responseDTO.getError() != null) {
            String errorCode = responseDTO.getError().getCode();
            String errorDesc = responseDTO.getError().getErrorDescription();
            String errorMessage = "Error from user status service: " + (errorDesc != null ? errorDesc : "Unknown error");
            log.error("User status service returned error code: {}, message: {}", errorCode, errorDesc);
            throw new CustomException(
                    IssuerResponseEnum.THIRD_PARTY_SERVICE_ERROR.getErrorCodes(),
                    "Failed to retrieve user status.",
                    errorMessage,
                    ErrorKind.ERR_KIND_CS);
        }

        FetchCustIdRespDTO body = responseDTO.getBody();
        if (body == null) {
            String errorMessage = "User status response body is null.";
            log.error(errorMessage);
            throw new CustomException(
                    IssuerResponseEnum.THIRD_PARTY_INVALID_RESPONSE.getErrorCodes(), 
                    "Failed to retrieve user status.", 
                    errorMessage, 
                    ErrorKind.ERR_KIND_CS);
        }

        String custId = body.getCustomerId();
        if (StringUtils.hasText(custId)) {
            return custId.replaceAll("\"", "");
        } else {
            String errorMessage = "Unexpected response format. Plain customer ID field is empty.";
            log.error(errorMessage);
            throw new CustomException(
                    IssuerResponseEnum.THIRD_PARTY_INVALID_RESPONSE.getErrorCodes(),
                    "Invalid response from user status service.",
                    errorMessage,
                    ErrorKind.ERR_KIND_CS);
        }
    }
}
