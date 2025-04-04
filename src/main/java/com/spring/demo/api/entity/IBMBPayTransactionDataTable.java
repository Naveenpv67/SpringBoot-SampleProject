package com.example.paytransaction.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ibmb_pay_transaction_data")
@NamedQuery(name = "IBMBPayTransactionDataTable.findAll", query = "SELECT n FROM IBMBPayTransactionDataTable n")
public class IBMBPayTransactionDataTable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ibmb_pay_transaction_data_ID_SEQ", sequenceName = "ibmb_pay_transaction_data_ID_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ibmb_pay_transaction_data_ID_SEQ")
    private Integer id;

    @Column(name = "reference_id")
    private String referenceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ack_response_json", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> ackResponseJson = new HashMap<>();

    @Column(name = "msg_id")
    @Size(max = 35)
    private String msgId;

    @Column(name = "org_id")
    @Size(max = 5)
    private String orgId;

    @Column(name = "correlation_key")
    @Size(max = 255)
    private String correlationKey;

    @Column(name = "ibmb_merchant_id")
    @Size(max = 15)
    private String ibmbMerchantId;

    @Column(name = "paid")
    @Size(max = 5)
    private String paid;

    @Column(name = "request_ts")
    private Timestamp requestTs;

    @Column(name = "amount")
    private BigDecimal amount;

    @Size(max = 3)
    private String currency;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ibmb_payload", columnDefinition = "jsonb", length = 1000)
    private String ibmbPayload;

    @Column(name = "ibmb_response_ts")
    private Timestamp ibmbResponseTs;

    @Column(name = "err_code")
    @Size(max = 20)
    private String errCode;

    @Column(name = "err_reason")
    @Size(max = 255)
    private String errReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_json", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> requestJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> payload = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status = StatusEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private ResultEnum result = ResultEnum.NOT_STARTED;
}
