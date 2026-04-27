import lombok.Data;
import java.io.Serializable;

/**
 * Optimized Cache DTO for Issuer Fetch Request
 */
@Data
public class ReqFetchCacheDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String pk; // Mapped from referenceId
    private String deviceApp;
    private String deviceBrowser;
    private String deviceOs;
    private String deviceId;
    private String initiationMode;
    private String mobileNo;
}
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReqFetchCacheMapper {

    ReqFetchCacheMapper INSTANCE = Mappers.getMapper(ReqFetchCacheMapper.class);

    @Mapping(source = "referenceId", target = "pk")
    ReqFetchCacheDTO toCacheDto(IssuerFetchRequestTable entity);

    @Mapping(source = "pk", target = "referenceId")
    IssuerFetchRequestTable toEntity(ReqFetchCacheDTO dto);
}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReqFetchCacheDAO {

    // Aerospike 'Set' name for this specific functional area
    private static final String SET_NAME = "req_fetch_cache";
    
    // TTL in seconds (e.g., 900s = 15 minutes)
    private static final int TTL_SECONDS = 900; 

    @Autowired
    private AerospikeUtil aerospikeUtil;

    /**
     * Write operation: Save DTO to Aerospike
     */
    public void save(ReqFetchCacheDTO dto) {
        if (dto != null && dto.getPk() != null) {
            aerospikeUtil.addUpdateCache(
                SET_NAME, 
                dto.getPk(), 
                TTL_SECONDS, 
                dto
            );
        }
    }

    /**
     * Read operation: Fetch DTO from Aerospike
     */
    public ReqFetchCacheDTO findByPk(String pk) {
        if (pk == null || pk.isEmpty()) {
            return null;
        }
        return aerospikeUtil.getRecord(
            SET_NAME, 
            pk, 
            ReqFetchCacheDTO.class
        );
    }
}


