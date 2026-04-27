@Slf4j
@Component
public class IssuerFetchDataProvider {

    @Autowired
    private ReqFetchCacheDAO cacheDAO; 

    @Autowired
    private IssuerFetchRequestTableRepository repository; 

    @Autowired
    private ReqFetchCacheMapper mapper;

    // A specific tag for this functional area to make Splunk/ELK filtering easy
    private static final String LOG_TAG = "[REQ-FETCH]";

    public IssuerFetchRequestTable getIssuerFetchRequestTable(String referenceId) {
        
        // 1. Attempt Cache Read
        try {
            ReqFetchCacheDTO cachedDto = cacheDAO.getCachedDto(referenceId);
            if (cachedDto != null) {
                // Log is now specific to THIS data source
                log.info("{} Cache Hit for RefID: {}", LOG_TAG, referenceId);
                return mapper.toEntity(cachedDto);
            }
        } catch (Exception e) {
            log.error("{} Cache lookup failed for RefID: {}. Fallback to DB.", LOG_TAG, referenceId);
        }

        // 2. Cache Miss: Fallback to DB
        log.info("{} Cache Miss. Querying DB for RefID: {}", LOG_TAG, referenceId);
        try {
            IssuerFetchRequestTable entity = repository.findByReferenceId(referenceId);
            
            if (entity != null) {
                // 3. Update Cache for the next call in the payment flow
                log.info("{} Refreshing Aerospike for RefID: {}", LOG_TAG, referenceId);
                cacheDAO.cacheEntity(entity); 
                return entity;
            }
        } catch (Exception e) {
            log.error("{} DB lookup failed for RefID: {}. Error: {}", 
                      LOG_TAG, referenceId, ExceptionUtils.getMessage(e));
        }

        return null;
    }
}
