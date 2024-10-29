package com.example.paytransaction.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.paytransaction.enums.ResultEnum;
import com.example.paytransaction.enums.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "client_payment_requests")
@Data
public class ClientPaymentRequestEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "client_payment_request_id_seq", sequenceName = "client_payment_request_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_payment_request_id_seq")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "remit_account_no")
    private String remitAccountNo;

    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    @Column(name = "ts")
    private String ts;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "nbbl_merchant_id")
    private String nbblMerchantId;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "mcc")
    private String mcc;

    @Column(name = "issuer_bank_id")
    private String issuerBankId;

    @Column(name = "transaction_ts")
    private String transactionTs;

    @Column(name = "initiation_mode")
    private String initiationMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private ResultEnum result = ResultEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status = StatusEnum.NOT_STARTED;

    @Column(name = "err_code")
    private String errCode;

    @Column(name = "err_reason")
    private String errReason;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "net_b_type")
    private String netBType;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "txn_id")
    private String txnID;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "note")
    private String note;

    @Column(name = "initiation_mode_transaction")
    private String initiationModeTransaction;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_request_json", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> paymentRequestJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_response_json", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> paymentResponseJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "error_payload", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> errorPayload = new HashMap<>();
}
