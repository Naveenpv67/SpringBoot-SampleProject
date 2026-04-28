@Slf4j
public abstract class AbstractCacheOnlyManager<E> extends AbstractBasePersistenceManager<E> {

    // Only Cache hooks are required
    protected abstract void cacheWrite(String id, E entity);
    protected abstract E cacheRead(String id);

    @Override
    public void save(E entity) {
        String id = getIdentifier(entity);
        try {
            logProcess("START", "CACHE-SAVE", id, "Pure-Memory Persistence");
            
            cacheWrite(id, entity);
            
            logProcess("SUCCESS", "CACHE-SAVE", id, "Stored in Aerospike");
        } catch (Exception e) {
            logCriticalError("CACHE-SAVE", id, e);
            throw e; 
        }
    }

    @Override
    public E find(String id) {
        try {
            logProcess("START", "CACHE-FETCH", id, "Direct Memory Lookup");
            
            E cached = cacheRead(id);
            
            if (cached != null) {
                logProcess("HIT", "CACHE-FETCH", id, null);
            } else {
                logProcess("NOT-FOUND", "CACHE-FETCH", id, "No ephemeral data exists");
            }
            return cached;
            
        } catch (Exception e) {
            logCriticalError("CACHE-FETCH", id, e);
            return null; // For cache-only, if cache is down, data is lost
        }
    }
}
