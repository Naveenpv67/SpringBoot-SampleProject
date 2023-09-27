public class RequestValidationException extends RuntimeException {

    private final String errorMessage;

    public RequestValidationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}




import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<String> handleRequestValidationException(RequestValidationException ex) {
        // Return a response entity with the error message and HTTP status code 400 (Bad Request)
        return new ResponseEntity<>(ex.getErrorMessage(), HttpStatus.BAD_REQUEST);
    }
}
