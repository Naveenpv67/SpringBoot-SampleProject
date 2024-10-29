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
import lombok.Data;

@Entity
@Table(name = "ibmb_request_resp_pay_txn_table")
@NamedQuery(name = "IBMBRequestRespPayTxnTable.findAll", query = "SELECT n FROM IBMBRequestRespPayTxnTable n")
@Data
public class IBMBRequestRespPayTxnTable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ibmb_request_resp_pay_txn_table_ID_SEQ", sequenceName = "ibmb_request_resp_pay_txn_table_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ibmb_request_resp_pay_txn_table_ID_SEQ")
    private Integer id;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "msg_id")
    private String msgId;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "correlation_key")
    private String correlationKey;

    @Column(name = "request_ts")
    private Timestamp requestTs;

    private BigDecimal amount;

    private String currency;

    @Column(name = "ibmb_response_ts")
    private Timestamp ibmbResponseTs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ack_response_json", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> ackResponseJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_json", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> requestJson = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> payload = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "error_payload", columnDefinition = "jsonb", length = 1000)
    private Map<String, Object> errorPayload = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "response_status")
    private StatusEnum responseStatus = StatusEnum.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private ResultEnum result = ResultEnum.NOT_STARTED;
}
