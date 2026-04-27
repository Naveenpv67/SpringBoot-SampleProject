/**
     * Maps Entity to DTO and persists to Aerospike.
     * Logic: Fail-safe (Catch exception so payment flow continues even if cache fails).
     */
    public void cacheEntity(IssuerFetchRequestTable entity) {
        if (entity == null) return;
        
        String pk = entity.getReferenceId();
        try {
            log.info("[CACHE-WRITE-START] Writing ReqFetch to Aerospike for RefID: {}", pk);
            
            ReqFetchCacheDTO dto = mapper.toCacheDto(entity);
            int ttl = ttlConfig.getReqFetch();
            
            aerospikeUtil.addUpdateCache(SET_NAME, pk, ttl, dto);
            
            log.info("[CACHE-WRITE-SUCCESS] ReqFetch persisted for RefID: {} | TTL: {}s", pk, ttl);
        } catch (Exception e) {
            log.error("[CACHE-WRITE-FAIL] Failed to write cache for RefID: {}. Error: {}", 
                      pk, ExceptionUtils.getMessage(e));
            // We do not throw the exception; payment flow should fall back to DB
        }
    }

    /**
     * Fetches DTO from Aerospike.
     * Logic: Logs Hits and Misses for performance monitoring.
     */
    public ReqFetchCacheDTO getCachedDto(String pk) {
        try {
            log.info("[CACHE-FETCH-START] Looking up ReqFetch in Aerospike for RefID: {}", pk);
            
            ReqFetchCacheDTO dto = aerospikeUtil.getRecord(SET_NAME, pk, ReqFetchCacheDTO.class);
            
            if (dto != null) {
                log.info("[CACHE-FETCH-HIT] Data retrieved from Aerospike for RefID: {}", pk);
            } else {
                log.info("[CACHE-FETCH-MISS] No cache found for RefID: {}. Falling back to DB.", pk);
            }
            return dto;
            
        } catch (Exception e) {
            log.error("[CACHE-FETCH-ERROR] Aerospike lookup failed for RefID: {}. Error: {}", 
                      pk, ExceptionUtils.getMessage(e));
            return null; // Return null so service layer initiates DB fallback
        }
    }
