Role:
You are a Principal Senior Software Architect with deep expertise in Spring Boot recognized for delivering highly robust, resilient, and maintainable distributed systems in fintech and payment domains.

Context:
The acquirer-service currently exposes the ReqTxnInit API as a REST endpoint. The requirement is to make this endpoint as asynchronous that is 
via concept of sse (Server sent events),  so 1st front end will call init api (acquirer-service) then after that acquirer-service will call nbbl api for the payment initiation request.
then after calling nbbl it will return the acknowledgment response (success/failure) immediately.
then if the acknowledgment response is failure or success then we will return the response to the front end immediately.
based on the acknowledgment response if it is failure then front end will not call any further api and will return the status as failed to the user.
if the acknowledgment response is success then front end will call our newly exposed api watch-status api for the referenceId (which has to be exposed newly and return the SSE Object, that new api produces stream type)
then after that nbbl will call our callback api (that is already existing api in acquirer-service) with the actual payment transaction response.
then after getting the actual payment transaction response from nbbl via callback api, we have to send that response to the front end via sse stream (that is watch-status api).

next step : nbbl will call our existing callback api with the actual payment transaction response.
in AMPSAcquirerServiceImpl method handleResponseInitTransaction
this method is triggered by the nbbl (actual callback method) 
so in this method after processing the actual payment transaction response from nbbl we have to send that response to the front end via sse stream (that is watch-status api).

I've already implemented the PaymentService class which contains the sse logic. with all scenarios handled like creating an in memory cache for the status until consumed and how the SseEmitter should be there.
different types of in-memory cache for status until consumed (refId -> CacheEntry) with ttl and emitter timeout etc.
Now I need your help to implement the following two methods in the AMPSAcquirerServiceImpl class.

// Complete Bussiness logic is given below for your reference

@Service
@EnableScheduling
public class PaymentService {
    // Map of refId to SseEmitter
    private final ConcurrentMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // In-memory cache for status until consumed (refId -> CacheEntry)
    private static class CacheEntry {
        final PaymentStatusResponse response;
        final long createdAt;
        CacheEntry(PaymentStatusResponse response) {
            this.response = response;
            this.createdAt = System.currentTimeMillis();
        }
    }
    private final ConcurrentMap<String, CacheEntry> statusCache = new ConcurrentHashMap<>();

    // TTL in ms for status cache (e.g., 30 seconds)
    private static final long STATUS_CACHE_TTL_MS = 60_000;
    // Timeout for SSE emitters (e.g., 5 seconds)
    private static final long EMITTER_TIMEOUT_MS = 60_000;

    public PaymentService() {}


    public String createTransaction(String refId) {
        // Optionally, store INITIATED status in cache if you want to always show initial state
        // statusCache.put(refId, "INITIATED");
        return "Transaction registered for refId: " + refId;
    }
    public SseEmitter getStatusStream(String refId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emitters.put(refId, emitter);

        // Remove emitter on completion/error/timeout
        Runnable cleanup = () -> emitters.remove(refId);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        // Check in-memory cache for status
        sendAndCompleteIfCached(emitter, refId);
        // else: wait for callback to arrive
        return emitter;
    }

    public void completeTransaction(String refId, PaymentStatusResponse statusResponse) {
        SseEmitter emitter = emitters.remove(refId);
        if (emitter != null) {
            sendAndComplete(emitter, statusResponse);
        } else {
            // Cache the status in memory for later consumption
            statusCache.put(refId, new CacheEntry(statusResponse));
        }
    }

    // Utility method to send and complete emitter with error handling
    private void sendAndComplete(SseEmitter emitter, PaymentStatusResponse response) {
        try {
            emitter.send(response);
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
// Utility method to check cache and send/complete if present
    private void sendAndCompleteIfCached(SseEmitter emitter, String refId) {
        CacheEntry cached = statusCache.remove(refId);
        if (cached != null) {
            sendAndComplete(emitter, cached.response);
        }
    }

    // Scheduled cleanup for statusCache (TTL)
    @Scheduled(fixedDelay = 10_000)
    public void cleanupStatusCache() {
        long now = System.currentTimeMillis();
        statusCache.entrySet().removeIf(entry -> now - entry.getValue().createdAt > STATUS_CACHE_TTL_MS);
    }

}
