import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tpt_limit_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TptLimitAudit {

    @Id
    @Column(name = "ref_id", length = 60, nullable = false)
    private String refId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    // HashCast ID
    @Column(name = "hash_cast_id", length = 60)
    private String hashCastId;

    // Channel
    @Column(name = "channel", length = 50)
    private String channel;

    // TPT Limit Value
    @Column(name = "tpt_limit_value", length = 50)
    private String tptLimitValue;

    // TPT Limit Result (Enum type can be added here if needed)
    @Enumerated(EnumType.STRING)
    @Column(name = "tpt_limit_result", length = 50)
    private ResultEnum tptLimitResult;

    // TPT Limit Timestamp
    @Column(name = "tpt_limit_ts")
    private Timestamp tptLimitTimestamp;

    // Request Payload (JSONB column)
    @Column(name = "request_payload", columnDefinition = "jsonb", length = 3000)
    private String requestPayload;

    // Response Payload (JSONB column)
    @Column(name = "response_payload", columnDefinition = "jsonb", length = 3000)
    private String responsePayload;
}


---

CREATE TABLE tpt_limit_audit (
    ref_id VARCHAR(60) NOT NULL,  -- Part of the composite primary key
    request_type VARCHAR(50) NOT NULL,  -- Part of the composite primary key
    hash_cast_id VARCHAR(60),  -- HashCast ID
    channel VARCHAR(50),  -- Channel
    tpt_limit_value VARCHAR(50),  -- TPT Limit Value
    tpt_limit_result VARCHAR(50),  -- TPT Limit Result (Enum type)
    tpt_limit_ts TIMESTAMP,  -- TPT Limit Timestamp
    request_payload JSONB,  -- Request Payload (JSONB)
    response_payload JSONB,  -- Response Payload (JSONB)
    
    -- Composite Primary Key
    CONSTRAINT pk_tpt_limit_audit PRIMARY KEY (ref_id, request_type)
);

---

public enum RequestType {
    PAYMENT,
    REQUEST_FETCH
}
