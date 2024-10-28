package com.example.paytransaction.repo;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.paytransaction.dto.PaymentRequest;
import com.example.paytransaction.dto.PaymentRequest.PayRequestTransaction;
import com.example.paytransaction.entity.ClientPaymentRequestEntity;
import com.example.paytransaction.enums.ResultEnum;
import com.example.paytransaction.enums.StatusEnum;

import jakarta.transaction.Transactional;

@Repository
public class ClientPaymentRequestDAO {

    @Autowired
    private ClientPaymentRequestRepository clientPaymentRequesRepository;

    @Transactional
    public ClientPaymentRequestEntity savePaymentRequest(PaymentRequest paymentRequest) {
        ClientPaymentRequestEntity entity = new ClientPaymentRequestEntity();
        
        // Set fields from PaymentRequest
        entity.setUserId(paymentRequest.getUserId());
        entity.setCustomerId(paymentRequest.getCustomerId());
        entity.setAccountNo(paymentRequest.getAccountNo());
        entity.setRemitAccountNo(paymentRequest.getRemitAccountNo());
        
        PayRequestTransaction transaction = paymentRequest.getPayRequestTransaction();
        if (transaction != null) {
            entity.setRefNo(transaction.getRefNo());
            entity.setNbblMerchantId(transaction.getNbblMerchantId());
            entity.setMerchantName(transaction.getMerchantName());
            entity.setMcc(transaction.getMcc());
            entity.setIssuerBankId(transaction.getIssuerBankId());
            entity.setTransactionTs(transaction.getTranscationTs());
            entity.setInitiationMode(transaction.getInitiationMode());
            entity.setResult(ResultEnum.INITIATED);
            entity.setStatus(StatusEnum.INITIATED);
            entity.setErrCode(transaction.getErrCode());
            entity.setErrReason(transaction.getErrReason());
            entity.setAmount(transaction.getAmount());
            entity.setCurrency(transaction.getCurrency());
            entity.setNetBType(transaction.getNetBType());
            entity.setRemarks(transaction.getRemarks());
        }
        // Populate paymentRequestJson with data from the PaymentRequest object
        Map<String, Object> requestJson = new HashMap<>();
        requestJson.put("data", paymentRequest);
        entity.setPaymentRequestJson(requestJson);

        // Save entity using DAO
        clientPaymentRequesRepository.save(entity);
        
        return entity;
    }
}
