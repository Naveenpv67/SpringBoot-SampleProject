public class ErrorConstants {
    // The single source of truth for all technical failure messages shown to customers.
    public static final String GENERIC_FAILURE_MSG = "Unable to process your transaction at this time. Please try again later.";
}

import lombok.Getter;

@Getter
public enum IssuerResponseEnum {

    // =============================================================================
    // GENERAL ERRORS (GEN)
    // =============================================================================
    SUCCESS("ERR_ISS_GEN_000", "Success", ErrorKind.ERR_KIND_OTHER),
    INTERNAL_SERVER_ERROR("ERR_ISS_GEN_001", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_INTERNAL),
    UNAUTHORIZED("ERR_ISS_GEN_002", "Session expired. Please login again.", ErrorKind.ERR_KIND_UNAUTHORIZED),
    METHOD_NOT_ALLOWED("ERR_ISS_GEN_003", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_INVALID_REQUEST),
    SERVICE_UNAVAILABLE("ERR_ISS_GEN_004", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_IO),

    // =============================================================================
    // DATABASE ERRORS (DB)
    // =============================================================================
    DB_CONNECTION_ERROR("ERR_ISS_DB_001", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_DATABASE),
    DB_QUERY_ERROR("ERR_ISS_DB_002", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_DATABASE),
    DB_RECORD_NOT_FOUND("ERR_ISS_DB_003", "Requested information was not found.", ErrorKind.ERR_KIND_NOT_EXIST),
    DB_INTEGRITY_VIOLATION("ERR_ISS_DB_004", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_DATABASE),
    DB_UPDATE_FAILED("ERR_ISS_DB_005", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_DATABASE),

    // =============================================================================
    // CACHE ERRORS (CAC)
    // =============================================================================
    CACHE_CONNECTION_ERROR("ERR_ISS_CAC_001", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_IO),
    CACHE_KEY_NOT_FOUND("ERR_ISS_CAC_002", "Session information has expired.", ErrorKind.ERR_KIND_NOT_EXIST),
    CACHE_WRITE_ERROR("ERR_ISS_CAC_003", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_IO),

    // =============================================================================
    // REQUEST & VALIDATION ERRORS (REQ)
    // =============================================================================
    MANDATORY_FIELD_MISSING("ERR_ISS_REQ_001", "Required information is missing.", ErrorKind.ERR_KIND_VALIDATION),
    INVALID_ARGUMENT_TYPE("ERR_ISS_REQ_002", "Invalid data format provided.", ErrorKind.ERR_KIND_INVALID_REQUEST),
    REF_ID_MISMATCH("ERR_ISS_REQ_003", "Transaction reference mismatch detected.", ErrorKind.ERR_KIND_INVALID),
    REF_ID_EXISTS("ERR_ISS_REQ_004", "This transaction reference has already been used.", ErrorKind.ERR_KIND_EXIST),
    DUPLICATE_SESSION("ERR_ISS_REQ_005", "Multiple active sessions detected.", ErrorKind.ERR_KIND_EXIST),

    // =============================================================================
    // EXTERNAL GATEWAY / WEBCLIENT ERRORS (WCL)
    // =============================================================================
    WCL_CONNECTION_ERROR("ERR_ISS_WCL_001", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_IO),
    WCL_TIMEOUT("ERR_ISS_WCL_002", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_IO),
    WCL_BAD_GATEWAY("ERR_ISS_WCL_003", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_IO),
    WCL_RESPONSE_INVALID("ERR_ISS_WCL_004", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_UNANTICIPATED),

    // =============================================================================
    // BUSINESS LOGIC & TPT ERRORS (BIZ)
    // =============================================================================
    TPT_NOT_REGISTERED("ERR_ISS_BIZ_001", "Customer is not TPT Registered.", ErrorKind.ERR_KIND_NOT_ACTIVE),
    TPT_LIMIT_EXCEEDED("ERR_ISS_BIZ_002", "Maximum transfer limit of 50,000 exceeded within 48 hours of TPT activation.", ErrorKind.ERR_KIND_VALIDATION),
    TPT_ACTIVATION_PENDING("ERR_ISS_BIZ_003", "TPT activation is not complete. Please wait for 24 hours.", ErrorKind.ERR_KIND_NOT_ACTIVE),
    INSUFFICIENT_FUNDS("ERR_ISS_BIZ_004", "Insufficient funds in the account.", ErrorKind.ERR_KIND_VALIDATION),
    ACCOUNT_BLOCKED("ERR_ISS_BIZ_005", "This account is currently restricted.", ErrorKind.ERR_KIND_FORBIDDEN),
    DUPLICATE_TRANSACTION("ERR_ISS_BIZ_006", "Duplicate Transaction detected.", ErrorKind.ERR_KIND_EXIST),

    // =============================================================================
    // SECURITY & ENCRYPTION ERRORS (SEC)
    // =============================================================================
    ENCRYPTION_FAILED("ERR_ISS_SEC_001", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_ENC_DEC),
    DECRYPTION_FAILED("ERR_ISS_SEC_002", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_ENC_DEC),
    KEY_EXCHANGE_FAILED("ERR_ISS_SEC_003", ErrorConstants.GENERIC_FAILURE_MSG, ErrorKind.ERR_KIND_ENC_DEC);

    private final String errorCode;      // Format: ERR_ISS_CAT_001
    private final String customerMessage; // Final UI Message
    private final ErrorKind errorKind;    // Maps to HTTP Status via ErrorKind.getHttpStatus()

    IssuerResponseEnum(String errorCode, String customerMessage, ErrorKind errorKind) {
        this.errorCode = errorCode;
        this.customerMessage = customerMessage;
        this.errorKind = errorKind;
    }

    /**
     * Used by the Interceptor to set the final HTTP Response Status.
     */
    public org.springframework.http.HttpStatus getHttpStatus() {
        return ErrorKind.getHttpStatus(this.errorKind);
    }
}


import lombok.Getter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * CustomException: The core exception class for the Payment System.
 * It strictly separates Customer-Facing messages from Technical Developer details.
 */
@Getter
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** The Source of Truth - contains Code, Sanitized Message, and ErrorKind */
    private final IssuerResponseEnum responseEnum;

    /** 
     * Technical details for SREs/Developers. 
     * NEVER send this to the UI/Customer.
     */
    private final String devError;

    /** 
     * Contextual metadata (e.g., transactionId, userId, attemptCount).
     * Helps in log aggregation and debugging.
     */
    private final Map<String, Object> metadata;

    /**
     * Standard Constructor
     * @param responseEnum The categorized error from our Master Enum
     * @param devError The "Ugly Truth" (e.g., "SQL Timeout on Cluster B")
     */
    public CustomException(IssuerResponseEnum responseEnum, String devError) {
        // We pass the Sanitized Customer Message to the parent RuntimeException
        super(responseEnum.getCustomerMessage());
        this.responseEnum = responseEnum;
        this.devError = devError;
        this.metadata = new HashMap<>();
    }

    /**
     * Enhanced Constructor with Metadata
     */
    public CustomException(IssuerResponseEnum responseEnum, String devError, Map<String, Object> metadata) {
        super(responseEnum.getCustomerMessage());
        this.responseEnum = responseEnum;
        this.devError = devError;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Fluently add metadata to the exception for better logging.
     * Usage: throw new CustomException(ENUM, "msg").addContext("refId", refId);
     */
    public CustomException addContext(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    // =============================================================================
    // HELPER METHODS FOR INTERCEPTORS
    // =============================================================================

    /** 
     * Returns the unique Error Code (e.g., ERR_ISS_DB_001) 
     */
    public String getErrorCode() {
        return responseEnum.getErrorCode();
    }

    /** 
     * Returns the ErrorKind (e.g., ERR_KIND_DATABASE) 
     */
    public ErrorKind getKind() {
        return responseEnum.getErrorKind();
    }

    /** 
     * Returns the HTTP Status (e.g., 500, 404) via the ErrorKind mapping 
     */
    public int getHttpStatusCodeValue() {
        return responseEnum.getHttpStatus().value();
    }
}

import java.util.Map;
import java.util.HashMap;

/**
 * ExceptionHelper: The centralized "Utensil" for the Payment System.
 * Designed to make throwing robust, categorized exceptions effortless.
 */
public class ExceptionHelper {

    /**
     * The Standard "Prepare and Throw"
     * Use this for basic technical or business failures.
     */
    public static CustomException terminate(IssuerResponseEnum response, String technicalDetail) {
        return new CustomException(response, technicalDetail);
    }

    /**
     * The "Context-Rich" Throw
     * Use this when you have metadata (like referenceId, userId) to attach.
     */
    public static CustomException terminate(IssuerResponseEnum response, String technicalDetail, Map<String, Object> metadata) {
        return new CustomException(response, technicalDetail, metadata);
    }

    /**
     * The "Dynamic Technical Message" Throw
     * Allows developers to use String formatting (e.g., "User %s failed")
     */
    public static CustomException terminate(IssuerResponseEnum response, String techDetailFormat, Object... args) {
        String formattedDetail = String.format(techDetailFormat, args);
        return new CustomException(response, formattedDetail);
    }

    /**
     * Specialized: Database Record Not Found
     * Automatically maps to DB_RECORD_NOT_FOUND and creates a clear tech log.
     */
    public static CustomException terminateNotFound(String entityName, String id) {
        String techDetail = String.format("%s not found with ID: %s in the database.", entityName, id);
        return new CustomException(IssuerResponseEnum.DB_RECORD_NOT_FOUND, techDetail);
    }

    /**
     * Specialized: TPT Limit Violation
     * Automatically attaches the attempt and limit to metadata for auditing.
     */
    public static CustomException terminateLimitExceeded(double attempted, double limit, String userId) {
        Map<String, Object> context = new HashMap<>();
        context.put("attemptedAmount", attempted);
        context.put("limitValue", limit);
        context.put("userId", userId);
        
        return new CustomException(
            IssuerResponseEnum.TPT_LIMIT_EXCEEDED, 
            String.format("User %s hit TPT limit. Attempted: %f, Limit: %f", userId, attempted, limit),
            context
        );
    }
}

// usage
public Transaction fetchTransaction(String refId) {
    return repository.findByRefId(refId)
        .orElseThrow(() -> ExceptionHelper.terminateNotFound("Transaction", refId));
}

try {
    bankGateway.call(request);
} catch (TimeoutException e) {
    // Uses the Dynamic String formatting helper
    throw ExceptionHelper.terminate(
        IssuerResponseEnum.WCL_TIMEOUT, 
        "Bank API timed out after %d ms for Ref: %s", timeoutValue, refId
    );
}

if (isDuplicate(txRequest)) {
    Map<String, Object> meta = Map.of("originalTxId", existingId, "timestamp", System.currentTimeMillis());
    
    throw ExceptionHelper.terminate(
        IssuerResponseEnum.DUPLICATE_TRANSACTION, 
        "Duplicate detected for Ref: " + txRequest.getRefId(), 
        meta
    );
}
