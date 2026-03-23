import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handled Exception: Catches our specific CustomException.
     * Use this for all business and technical errors we anticipate.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorClass> handleCustomException(CustomException ex) {
        // 1. Generate a unique Trace ID (The bridge between Customer and Logs)
        String traceId = UUID.randomUUID().toString();
        
        // 2. Extract details from our Robust Enum
        IssuerResponseEnum errorEnum = ex.getResponseEnum();
        String errorCode = errorEnum.getErrorCode();
        HttpStatus status = ErrorKind.getHttpStatus(ex.getKind());

        // 3. INTERNAL LOGGING: Log the "Ugly Truth" for Developers/SREs
        // We log everything: Trace ID, the dev message, the metadata, and the full stack trace.
        log.error("TraceID: [{}] | ErrorCode: {} | Kind: {} | TechDetail: {} | Metadata: {} | Stack: {}", 
            traceId, 
            errorCode, 
            ex.getKind(), 
            ex.getDevError(), 
            ex.getMetadata(), 
            ExceptionUtils.getStackTrace(ex)
        );

        // 4. EXTERNAL RESPONSE: Populate the existing ErrorClass
        // MESSAGE: The "Polite Wall" Sanitized Message
        // DEV_ERROR: We show the Trace ID so the user can report it to support safely.
        ErrorClass errorResponse = new ErrorClass(
            errorCode, 
            ex.getMessage(), // Sanitized message from super()
            "Trace ID: " + traceId, // Safe info for customer
            ex.getKind()
        );

        return ResponseEntity.status(status)
                .header("X-Trace-ID", traceId)
                .header("X-Error-Code", errorCode)
                .body(errorResponse);
    }

    /**
     * Unhandled Exception: The Safety Net.
     * Catches NullPointer, SQL Syntax errors, etc., that we didn't catch in a try-block.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorClass> handleGenericException(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        
        // Log the raw system error so we can fix the bug
        log.error("TraceID: [{}] | UNHANDLED_EXCEPTION | Stack: {}", 
            traceId, ExceptionUtils.getStackTrace(ex));

        // Create a generic Polite Wall response
        ErrorClass errorResponse = new ErrorClass(
            IssuerResponseEnum.INTERNAL_SERVER_ERROR.getErrorCode(),
            ErrorConstants.GENERIC_FAILURE_MSG,
            "Trace ID: " + traceId,
            ErrorKind.ERR_KIND_INTERNAL
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Trace-ID", traceId)
                .body(errorResponse);
    }
}
