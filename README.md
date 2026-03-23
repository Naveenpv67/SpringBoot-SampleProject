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
import java.util.Optional;

/**
 * ExceptionHelper: The Ultimate Generic Utensil.
 * Optimized to prevent NullPointerExceptions and minimize boilerplate.
 */
public class ExceptionHelper {

    /**
     * LEVEL 1: The "Minimalist"
     * Use when the Enum itself is enough detail.
     * Prevents null checks by defaulting the tech detail to the Enum name.
     */
    public static CustomException terminate(IssuerResponseEnum response) {
        return new CustomException(response, "Technical reason not specified: " + response.name());
    }

    /**
     * LEVEL 2: The "Simple String" (The Dominant Method)
     * Use for direct technical messages WITHOUT formatting.
     * Handles null strings safely.
     */
    public static CustomException terminate(IssuerResponseEnum response, String technicalDetail) {
        String safeDetail = Optional.ofNullable(technicalDetail)
                .orElse("No technical detail provided for: " + response.getErrorCode());
        return new CustomException(response, safeDetail);
    }

    /**
     * LEVEL 3: The "Formatted Infinite"
     * Use when you need to inject variables into the log message.
     */
    public static CustomException terminate(IssuerResponseEnum response, String techMessage, Object... args) {
        // If args are null or empty, fall back to the Simple String method
        if (args == null || args.length == 0) {
            return terminate(response, techMessage);
        }
        
        try {
            String formattedDetail = String.format(techMessage, args);
            return new CustomException(response, formattedDetail);
        } catch (Exception e) {
            // If formatting fails (e.g. mismatch placeholders), don't crash. Log everything.
            return new CustomException(response, techMessage + " | Raw Args: " + java.util.Arrays.toString(args));
        }
    }

    /**
     * LEVEL 4: The "Context-Rich" (Metadata)
     * For when you need a Map for auditing/PII masking.
     */
    public static CustomException terminateWithContext(IssuerResponseEnum response, Map<String, Object> context, String techMessage, Object... args) {
        CustomException ex = terminate(response, techMessage, args);
        if (context != null) {
            ex.getMetadata().putAll(context);
        }
        return ex;
    }
}

1. Zero Null Risks
If a developer accidentally does this:
throw ExceptionHelper.terminate(IssuerResponseEnum.DB_CONNECTION_ERROR, null);
The system will not crash. The Optional.ofNullable inside the helper will catch it and log: "No technical detail provided for: ERR_ISS_DB_001".
2. Cleanest "Service Layer" Flow
You can now choose the shortest path based on the situation:
Case A: Just the error (No extra info needed)
code
Java
throw ExceptionHelper.terminate(IssuerResponseEnum.UNAUTHORIZED_ACCESS);
Case B: A simple message (No formatting overhead)
code
Java
throw ExceptionHelper.terminate(IssuerResponseEnum.DB_CONNECTION_ERROR, "Main Database is in Read-Only mode");
Case C: Full Diagnostic (The Infinite Path)
code
Java
throw ExceptionHelper.terminate(IssuerResponseEnum.TPT_LIMIT_EXCEEDED, 
    "User %s attempted %f. Limit is %f", userId, amount, limit);
3. High Performance
By providing the terminate(IssuerResponseEnum, String) overload, we skip the String.format engine entirely for simple messages. In a system handling 10,000 transactions per second, avoiding regex-based string formatting where it's not needed saves CPU cycles.
