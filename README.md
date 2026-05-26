public interface SdkKeyTableRepository extends JpaRepository<SdkKeyTable, Long> {
    
    // Check 1: Is the transaction itself a duplicate?
    boolean existsByReferenceId(String referenceId);

    // Check 2: Is the session key being reused?
    boolean existsBySdkSessionKey(String sdkSessionKey);
}

public ResponseDTO validateSdkKeyConflicts(ExchangeKeysDTO request, String referenceId) {

    // 1. Check Aerospike Cache First (Concurrency/Hot Duplicate Check)
    PaymentLifecycleContext context = lifecycleDataManager.find(referenceId);
    if (context != null) {
        log.warn("[ExchangeKeys - CACHE_DUPLICATE] RefId: {} is already in process", referenceId);
        return buildErrorResponse("AMPS-4009", "Transaction is already in progress. Please wait.");
    }

    // 2. Check DB for ReferenceId (Cold Duplicate Check)
    if (sdkKeyTableRepository.existsByReferenceId(referenceId)) {
        log.warn("[ExchangeKeys - DB_DUPLICATE_REF] RefId: {} already exists in DB", referenceId);
        return buildErrorResponse("AMPS-4100", "This transaction has already been completed. Please check your history.");
    }

    // 3. Check DB for SessionKey (Session Reuse Check)
    if (sdkKeyTableRepository.existsBySdkSessionKey(request.getSessionKey())) {
        log.warn("[ExchangeKeys - DB_DUPLICATE_SESSION] SessionKey: {} already exists", request.getSessionKey());
        return buildErrorResponse("AMPS-4101", "Invalid session details. Please re-initiate the payment process.");
    }

    return null; // All clear!
}

// Helper to keep code clean
private ResponseDTO buildErrorResponse(String code, String userMessage) {
    return getResponseDTO(
            AMPSIssuerConstants.CODE,
            AMPSIssuerConstants.FAILURE,
            code,
            userMessage
    );
}

public ResponseEntity<?> handleExchangeKeys(ExchangeKeysDTO request, String referenceId) {
    
    // 1. Run the new Conflict Validation
    ResponseDTO conflict = validateSdkKeyConflicts(request, referenceId);
    if (conflict != null) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(conflict);
    }

    // 2. Conflict passed! Initialize Lifecycle Cache
    PaymentLifecycleContext context = PaymentLifecycleContext.builder()
            .referenceId(referenceId)
            .currentLeg("EXCHANGE_KEYS")
            .stepResult("SUCCESS")
            .updatedAt(System.currentTimeMillis())
            .build();
    
    lifecycleDataManager.save(context);

    // 3. Proceed with existing exchange keys logic...
} 
