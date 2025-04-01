import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

@Service
public class TptLimitAuditService {

    @Autowired
    private TptLimitAuditRepository tptLimitAuditRepository;

    public TptLimitAudit saveTptLimitAudit(String refId, RequestType requestType, String hashCastId, 
                                           String channel, String requestAddition, String responseAddition) {
        // Create the composite key
        RefIdAndType id = new RefIdAndType(refId, requestType);
        
        // Create entity object
        TptLimitAudit tptLimitAudit = new TptLimitAudit();
        tptLimitAudit.setId(id);
        tptLimitAudit.setHashCastId(hashCastId);
        tptLimitAudit.setChannel(channel);
        tptLimitAudit.setRequestAddition(requestAddition);
        tptLimitAudit.setResponseAddition(responseAddition);
        tptLimitAudit.setTptLimitTimestamp(new Timestamp(System.currentTimeMillis())); // Current timestamp

        // Save entity in the database
        return tptLimitAuditRepository.save(tptLimitAudit);
    }
}
