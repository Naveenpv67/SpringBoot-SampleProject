@Slf4j
@Component
public class IssuerFetchDataProvider {

    @Autowired
    private ReqFetchCacheDAO cacheDAO; // Aerospike Logic

    @Autowired
    private IssuerFetchRequestTableRepository repository; // DB Logic

    @Autowired
    private ReqFetchCacheMapper mapper;

    /**
     * Unified method: Cache-first with DB Fallback
     */
    public IssuerFetchRequestTable getIssuerFetchRequestTable(String referenceId) {
        // 1. Attempt Cache Read
        try {
            ReqFetchCacheDTO cachedDto = cacheDAO.getCachedDto(referenceId);
            if (cachedDto != null) {
                log.info("[DATA-PROVIDER-HIT] Found in Aerospike for RefID: {}", referenceId);
                return mapper.toEntity(cachedDto); // Convert back to Entity for existing code compatibility
            }
        } catch (Exception e) {
            log.error("[DATA-PROVIDER-CACHE-ERROR] Cache lookup failed, proceeding to DB. RefID: {}", referenceId);
        }

        // 2. Cache Miss or Error: Fallback to DB
        log.info("[DATA-PROVIDER-MISS] Fetching from DB for RefID: {}", referenceId);
        try {
            IssuerFetchRequestTable entity = repository.findByReferenceId(referenceId);
            
            if (entity != null) {
                // 3. Asynchronous or Background Update of Cache (Don't block the main flow)
                cacheDAO.cacheEntity(entity); 
                return entity;
            }
        } catch (Exception e) {
            log.error("[DATA-PROVIDER-DB-ERROR] DB lookup failed for RefID: {}. Error: {}", 
                      referenceId, ExceptionUtils.getMessage(e));
        }

        return null;
    }
}
