package com.example.paytransaction.entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "payment_transaction_master")
@Data
public class PaymentTransactionMaster implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "payment_transaction_master_id_seq", sequenceName = "payment_transaction_master_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_transaction_master_id_seq")
    private Long id;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_request_json", columnDefinition = "jsonb")
    private Map<String, Object> clientRequestJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fc_request_json", columnDefinition = "jsonb")
    private Map<String, Object> fcRequestJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fc_response_json", columnDefinition = "jsonb")
    private Map<String, Object> fcResponseJson = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "fc_status")
    private StatusEnum fcStatus = StatusEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "fc_result")
    private ResultEnum fcResult = ResultEnum.NOT_STARTED;

    @Column(name = "fc_error_code")
    private String fcErrorCode;

    @Column(name = "fc_error_message")
    private String fcErrorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ibmb_request_json", columnDefinition = "jsonb")
    private Map<String, Object> ibmbRequestJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ibmb_ack_json", columnDefinition = "jsonb")
    private Map<String, Object> ibmbAckJson = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "ibmb_status")
    private StatusEnum ibmbStatus = StatusEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "ibmb_result")
    private ResultEnum ibmbResult = ResultEnum.NOT_STARTED;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ibmb_error_payload", columnDefinition = "jsonb")
    private Map<String, Object> ibmbErrorPayload = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "final_response_json", columnDefinition = "jsonb")
    private Map<String, Object> finalResponseJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "final_acknowledgment_json", columnDefinition = "jsonb")
    private Map<String, Object> finalAcknowledgmentJson = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "final_status")
    private StatusEnum finalStatus = StatusEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_result")
    private ResultEnum finalResult = ResultEnum.NOT_STARTED;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
