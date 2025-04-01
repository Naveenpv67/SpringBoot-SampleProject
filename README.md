import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TptLimitAuditId implements Serializable {

    private String refId;
    private RequestType requestType;

    // You can add constructors and methods if necessary

}


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

    @EmbeddedId
    private TptLimitAuditId id;  // Composite primary key (refId and requestType)

    @Column(name = "hash_cast_id", length = 60)
    private String hashCastId;  // HashCast ID

    @Column(name = "channel", length = 50)
    private String channel;  // Channel

    @Column(name = "tpt_limit_value", length = 50)
    private String tptLimitValue;  // TPT Limit Value

    @Enumerated(EnumType.STRING)
    @Column(name = "tpt_limit_result", length = 50)
    private ResultEnum tptLimitResult;  // TPT Limit Result (Enum)

    @Column(name = "tpt_limit_ts")
    private Timestamp tptLimitTimestamp;  // TPT Limit Timestamp

    @Column(name = "request_payload", columnDefinition = "jsonb", length = 3000)
    private String requestPayload;  // Request Payload (JSONB)

    @Column(name = "response_payload", columnDefinition = "jsonb", length = 3000)
    private String responsePayload;  // Response Payload (JSONB)

}


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TptLimitAuditId implements Serializable {

    private String refId;  // Part of the composite key
    private RequestType requestType;  // Part of the composite key

    public TptLimitAuditId(String refId, RequestType requestType) {
        this.refId = refId;
        this.requestType = requestType;
    }
}

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TptLimitAuditRepository extends JpaRepository<TptLimitAudit, TptLimitAuditId> {

    // Example custom query method to find records by refId and requestType
    Optional<TptLimitAudit> findByRefIdAndRequestType(String refId, RequestType requestType);

    // Example custom query method to find records by refId (you can modify as needed)
    List<TptLimitAudit> findByRefId(String refId);

    // Example custom query method to find records by requestType (you can modify as needed)
    List<TptLimitAudit> findByRequestType(RequestType requestType);

    // Add any additional custom queries you may need
}
