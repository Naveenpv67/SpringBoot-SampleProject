import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * A filter for logging the details of incoming HTTP requests and responses.
 * This filter caches the request and response bodies to enable logging after processing.
 */
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final TransactionLoggingUtil transactionLoggingUtil;

    public LoggingFilter(TransactionLoggingUtil transactionLoggingUtil) {
        this.transactionLoggingUtil = transactionLoggingUtil;
    }

    /**
     * Filters the request and response to log transaction details.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during the filter process
     * @throws IOException if an I/O error occurs during the filter process
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        boolean isTransactionLogged = false;
        String errorMessage = null;
        String requestBody = extractRequestBody(wrappedRequest);
        String responseBody = "";

        try {
            log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            responseBody = extractResponseBody(wrappedResponse);

            log.info("Transaction Logging Initiated");

            TransactionLoggingRequest transactionLoggingRequest = transactionLoggingUtil.createTransactionLoggingRequest(
                    new TransactionEventPayload(
                            (String) request.getAttribute(TransactionUtilityConstants.CORRELATION_ID),
                            Optional.ofNullable((String) request.getAttribute(TransactionUtilityConstants.MAKER_TON_ID))
                                    .orElse((String) request.getAttribute(TransactionUtilityConstants.CHECKER_TON_ID))));

            extractUserAgent(request.getHeader(HttpHeaders.USER_AGENT), transactionLoggingRequest);
            populateMetaData(request, transactionLoggingRequest);

            if (response.getStatus() <= 300) {
                log.info("Logging successful transaction for request: {} {}", request.getMethod(), request.getRequestURI());
                transactionLoggingUtil.logSuccessfulTransaction(requestBody, responseBody, transactionLoggingRequest);
                isTransactionLogged = true;
            } else {
                log.warn("Logging failed transaction for request: {} {}", request.getMethod(), request.getRequestURI());
                ResponseDTO<?> responseDTO = mapper.readValue(responseBody, ResponseDTO.class);
                transactionLoggingUtil.logFailedTransaction(requestBody, responseBody,
                        Optional.ofNullable(responseDTO.getErrorCode()).map(Object::toString).orElse("UNKNOWN_ERROR"),
                        Optional.ofNullable(responseDTO.getMessage()).orElse("Unknown error message"),
                        transactionLoggingRequest);
                isTransactionLogged = true;
            }

        } catch (Exception e) {
            errorMessage = e.getMessage();
            int status = response.getStatus();

            if (!isTransactionLogged) {
                log.error("Exception occurred during transaction logging for request: {} {}", request.getMethod(), request.getRequestURI(), e);
                transactionLoggingUtil.logFailedTransaction(requestBody, responseBody,
                        String.valueOf(status), errorMessage, createFallbackTransactionLoggingRequest(request));
                isTransactionLogged = true;
            }
            throw e;
        } finally {
            log.debug("Finalizing response processing for request: {} {}", request.getMethod(), request.getRequestURI());
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * Extracts the request body from the cached request.
     *
     * @param request the wrapped HTTP request
     * @return the request body as a string
     */
    private String extractRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        return getStringValue(content, request.getCharacterEncoding());
    }

    /**
     * Extracts the response body from the cached response.
     *
     * @param response the wrapped HTTP response
     * @return the response body as a string
     */
    private String extractResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        return getStringValue(content, response.getCharacterEncoding());
    }

    /**
     * Converts byte array content to a string.
     *
     * @param content     the byte array content
     * @param charsetName the character encoding
     * @return the content as a string
     */
    private String getStringValue(byte[] content, String charsetName) {
        try {
            return new String(content, charsetName != null ? charsetName : StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            log.error("Error converting byte array to string", e);
            return "";
        }
    }

    /**
     * Extracts the user agent details and populates the transaction logging request.
     *
     * @param userAgent the user agent string
     * @param transactionLoggingRequest the transaction logging request
     */
    private void extractUserAgent(String userAgent, TransactionLoggingRequest transactionLoggingRequest) {
        // Implement user agent extraction logic here
        // Example extraction (simplified for illustration)
        log.debug("Extracting user agent details: {}", userAgent);
        if (userAgent != null) {
            transactionLoggingRequest.setDeviceName("Extracted Device Name");
            transactionLoggingRequest.setBrowserName("Extracted Browser Name");
            transactionLoggingRequest.setDeviceOsVersion("Extracted OS Version");
        }
    }

    /**
     * Populates the metadata into the transaction logging request.
     *
     * @param request the HTTP request
     * @param transactionLoggingRequest the transaction logging request
     */
    private void populateMetaData(HttpServletRequest request, TransactionLoggingRequest transactionLoggingRequest) {
        // Implement meta-data population logic here
        log.debug("Populating metadata for transaction logging request");
    }

    /**
     * Creates a fallback transaction logging request in case of failure.
     *
     * @param request the HTTP request
     * @return the fallback transaction logging request
     */
    private TransactionLoggingRequest createFallbackTransactionLoggingRequest(HttpServletRequest request) {
        log.debug("Creating fallback transaction logging request");
        return transactionLoggingUtil.createTransactionLoggingRequest(
                new TransactionEventPayload(
                        (String) request.getAttribute(TransactionUtilityConstants.CORRELATION_ID),
                        Optional.ofNullable((String) request.getAttribute(TransactionUtilityConstants.MAKER_TON_ID))
                                .orElse(Optional.ofNullable((String) request.getAttribute(TransactionUtilityConstants.CHECKER_TON_ID))
                                        .orElse(TransactionUtilityConstants.NA))));
    }
}



import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs a successful transaction.
 *
 * @param request             the request object
 * @param response            the response object
 * @param transactionRequest  the transaction logging request object
 */
public void logSuccessfulTransaction(Object request, Object response, TransactionLoggingRequest transactionRequest) {
    try {
        log.info("Initiating successful transaction logging.");

        // Set request and response details
        transactionRequest.setRequest(mapper.writeValueAsString(request));
        transactionRequest.setResponse(mapper.writeValueAsString(response));

        // Set transaction status and clear error details
        transactionRequest.setStatus(TransactionUtilityConstants.SUCCESS_STATUS);
        transactionRequest.setErrorResponseCode(TransactionUtilityConstants.NULL);
        transactionRequest.setErrorResponseMessage(TransactionUtilityConstants.NULL);

        // Populate null fields with default values
        transactionRequest = populateNullString(transactionRequest);

        // Set maker or checker details
        setMakerOrCheckerDetails(httpServletRequest, transactionRequest);

        // Set end timestamp
        transactionRequest.setEndTimestamp(TransactionUtilityUtils.getRfc3339Timestamp(ZonedDateTime.now()));

        log.info("Logging successful transaction details.");

        // Log the transaction
        TransactionLoggingRequest loggedRequest = transactionLoggingService.log(transactionRequest);
        log.info("Event Payload Data Response: {}", mapper.writeValueAsString(loggedRequest));

    } catch (Exception e) {
        log.error("Failed to publish the log to Transaction Utility.");
        log.error("Error: {}", e.getMessage(), e);
    }
}






import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Logs a failed transaction.
 *
 * @param request             the request object
 * @param response            the response object
 * @param errorCode           the error code
 * @param errorMessage        the error message
 * @param transactionRequest  the transaction logging request object
 */
public void logFailedTransaction(Object request, Object response, String errorCode, String errorMessage, TransactionLoggingRequest transactionRequest) {
    try {
        log.info("Initiating failed transaction logging.");

        // Set request details
        transactionRequest.setRequest(mapper.writeValueAsString(request));

        // Set response details, defaulting to "NULL" if the response is empty
        String responseString = ObjectUtils.isEmpty(response) 
                ? TransactionUtilityConstants.NULL 
                : mapper.writeValueAsString(response);
        transactionRequest.setResponse(StringUtils.defaultIfEmpty(responseString, TransactionUtilityConstants.NULL));

        // Set transaction status and error details
        transactionRequest.setStatus(TransactionUtilityConstants.FAILURE_STATUS);
        transactionRequest.setErrorResponseMessage(errorMessage);
        transactionRequest.setErrorResponseCode(errorCode);
        transactionRequest.setUiErrorMessage(errorMessage);

        // Set maker or checker details
        setMakerOrCheckerDetails(httpServletRequest, transactionRequest);

        // Set end timestamp
        transactionRequest.setEndTimestamp(TransactionUtilityUtils.getRfc3339Timestamp(ZonedDateTime.now()));

        // Populate null fields with default values
        transactionRequest = populateNullString(transactionRequest);

        log.info("Logging failed transaction details.");

        // Log the transaction
        transactionLoggingService.log(transactionRequest);
    } catch (Exception e) {
        log.error("Failed to publish the log to Transaction Utility.");
        log.error("Error: {}", e.getMessage(), e);
    }
}



import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging transaction details after saving a Workflow.
 */
@Aspect
@Component
public class WorkflowAspect {
    private static final Logger log = LoggerFactory.getLogger(WorkflowAspect.class);

    /**
     * Advice that executes after a Workflow is saved to log transaction details.
     *
     * @param workflow the Workflow object returned by the save method
     */
    @AfterReturning(pointcut = "execution(* com.hdfc.admin.repository.WorkflowRepository.save(..))", returning = "workflow")
    public void afterSaveWorkflow(Workflow workflow) {
        try {
            if (workflow != null) {
                String transactionId = determineTransactionId();
                log.info("Transaction ID set: {}", transactionId);
                log.info("Workflow transaction logged successfully for Workflow ID: {}", workflow.getId());
            } else {
                log.warn("Workflow object is null. No transaction to log.");
            }
        } catch (Exception e) {
            log.error("Error while logging workflow transaction: {}", e.getMessage(), e);
        }
    }

    /**
     * Determines the transaction ID to be used for logging.
     * 
     * @return the transaction ID
     */
    private String determineTransactionId() {
        // Assuming that the transaction ID is fetched from some context or request attributes
        String makerTransactionId = (String) request.getAttribute("MakerTransactionID");
        String checkerTransactionId = (String) request.getAttribute("CheckerTransactionID");

        return Optional.ofNullable(makerTransactionId)
                       .orElse(Optional.ofNullable(checkerTransactionId)
                                       .orElse("NA"));
    }
}

        // Log the transaction
        TransactionLoggingRequest loggedRequest = transactionLoggingService.log(transactionRequest);
        log.info("Event Payload Data Response: {}", mapper.writeValueAsString(loggedRequest));

    } catch (Exception e) {
        log.error("Failed to publish the log to Transaction Utility.");
        log.error("Error: {}", e.getMessage(), e);
    }
}




// interface logging related code

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

/**
 * Service for logging failed interface transactions.
 */
@Service
public class InterfaceLoggingService {

    private static final Logger log = LoggerFactory.getLogger(InterfaceLoggingService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Logs a failed interface transaction.
     *
     * @param request                the request object
     * @param response               the response object
     * @param errorCode              the error code
     * @param errorMessage           the error message
     * @param interfaceLoggingRequest the interface logging request object
     */
    public void logFailedTransaction(Object request, Object response, String errorCode, String errorMessage,
                                     InterfaceLoggingRequest interfaceLoggingRequest) {
        try {
            // Set end timestamp
            interfaceLoggingRequest.setEndTimestamp(TransactionUtilityUtils.getRfc3339Timestamp(ZonedDateTime.now()));

            // Set error details
            interfaceLoggingRequest.setErrorResponseCode(errorCode);
            interfaceLoggingRequest.setErrorResponseMessage(errorMessage);

            // Set request and response details
            interfaceLoggingRequest.setRequest(mapper.writeValueAsString(request));
            interfaceLoggingRequest.setResponse(mapper.writeValueAsString(response));

            // Set status and correlation ID
            interfaceLoggingRequest.setStatus(InterfaceLoggingConstants.FAILURE);
            interfaceLoggingRequest.setCorrelationId((String) httpServletRequest.getAttribute(TransactionUtilityConstants.CORRELATION_ID));

            // Populate null strings
            interfaceLoggingRequest = populateNullString(interfaceLoggingRequest);

            // Log the failed transaction
            log.info("Logging failed interface transaction with Correlation ID: {}", interfaceLoggingRequest.getCorrelationId());
            interfaceLoggingService.log(interfaceLoggingRequest);
            log.info("Failed interface transaction logged: {}", interfaceLoggingRequest);
        } catch (Exception e) {
            log.error("Failed to log failed interface transaction: {}", e.getMessage(), e);
        }
    }

    /**
     * Populates null strings in the InterfaceLoggingRequest object.
     *
     * @param request the interface logging request object
     * @return the populated interface logging request object
     */
    private InterfaceLoggingRequest populateNullString(InterfaceLoggingRequest request) {
        // Implementation of populateNullString to handle null values
        // ...
        return request;
    }
}




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Service for creating an interface logging request with mandatory fields.
 */
@Service
public class InterfaceLoggingService {

    private static final Logger log = LoggerFactory.getLogger(InterfaceLoggingService.class);

    /**
     * Creates an interface logging request with mandatory fields.
     *
     * @param loggingMandatoryParams the mandatory parameters for interface logging
     * @return the interface logging request object with mandatory fields set
     */
    public InterfaceLoggingRequest createLoggingRequestWithMandatoryFields(InterfaceLoggingMandatoryParams loggingMandatoryParams) {
        log.info("Interface Logging Initiated");

        InterfaceLoggingRequest interfaceLoggingRequest = null;
        httpServletRequest.setAttribute("interfaceLogExists", true);

        try {
            String host = getServerName(); // Retrieve server name

            // Build the interface logging request
            interfaceLoggingRequest = InterfaceLoggingRequest.builder()
                    .channelId(InterfaceLoggingConstant.CHANNEL_ID)
                    .channelPlatform(InterfaceLoggingConstant.CHANNEL_PLATFORM)
                    .appId(InterfaceLoggingConstant.APP_ID)
                    .transactionLoggerUuid(UUID.randomUUID().toString())
                    .startTimestamp(TransactionUtilityUtils.getRfc3339Timestamp(ZonedDateTime.now()))
                    .sessionId(InterfaceLoggingConstant.NA) // Optional
                    .serverName(host)
                    .additionalInfo(InterfaceLoggingConstant.NA)
                    .errorResponseCode(InterfaceLoggingConstant.NA)
                    .errorResponseMessage(InterfaceLoggingConstant.NA)
                    .response(InterfaceLoggingConstant.NA)
                    .build();
        } catch (Exception e) {
            log.error("Failed to create interface logging request: {}", e.getMessage(), e);
        }

        return interfaceLoggingRequest;
    }

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    private String getServerName() {
        // Implementation to retrieve server name
        // ...
        return "localhost"; // Placeholder for server name
    }
}



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Component for interface logging calls.
 */
@Component
public class InterfaceLoggingComponent {

    private static final Logger log = LoggerFactory.getLogger(InterfaceLoggingComponent.class);

    @Autowired
    private InterfaceLoggingUtils interfaceLoggingUtils;

    @Autowired
    private HttpServletRequest request;

    /**
     * Logs interface call with relevant details.
     *
     * @param requestBody   the request body
     * @param callUrl       the URL of the call
     * @param queryParams   the query parameters of the call
     * @param response      the response object
     * @param successTxn    flag indicating whether the transaction was successful
     * @param errorCode     the error code, if any
     * @param errorMessage  the error message, if any
     */
    private void interfaceLoggingCall(Object requestBody, String callUrl, MultiValueMap<String, String> queryParams,
                                       Object response, boolean successTxn, String errorCode, String errorMessage) {
        try {
            String correlationId = (String) request.getAttribute(TransactionUtilityConstants.CORRELATION_ID);
            if (Objects.isNull(correlationId)) {
                log.info("Skipping Interface logging as there is no Transaction Logging for this request");
                return;
            }

            InterfaceLoggingMandatoryParams interfaceLoggingMandatoryParams = InterfaceLoggingMandatoryParams.builder().build();
            InterfaceLoggingRequest interfaceLoggingRequest = interfaceLoggingUtils.createLoggingRequestWithMandatoryFields(interfaceLoggingMandatoryParams);

            interfaceLoggingRequest.setEndpoint(callUrl);
            interfaceLoggingRequest.setServerName(InterfaceLoggingConstant.SERVER_COMMON_SERVICE);

            if (successTxn) {
                log.info("Logging successful Transaction-Interface logging");
                interfaceLoggingUtils.logSuccessfulTransaction(requestBody, response, interfaceLoggingRequest);
            } else {
                log.info("Logging failed Transaction-Interface logging");
                interfaceLoggingUtils.logFailedTransaction(requestBody, response, errorCode, errorMessage, interfaceLoggingRequest);
            }
        } catch (Exception exception) {
            log.error("Error occurred during interface logging: {}", exception.getMessage(), exception);
            throw exception;
        }
    }
}



import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

/**
 * Service for logging successful interface transactions.
 */
@Service
public class InterfaceLoggingService {

    private static final Logger log = LoggerFactory.getLogger(InterfaceLoggingService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Logs a successful interface transaction.
     *
     * @param request                the request object
     * @param response               the response object
     * @param interfaceLoggingRequest the interface logging request object
     */
    public void logSuccessfulTransaction(Object request, Object response,
                                         InterfaceLoggingRequest interfaceLoggingRequest) {
        try {
            // Set end timestamp
            interfaceLoggingRequest.setEndTimestamp(TransactionUtilityUtils.getRfc3339Timestamp(ZonedDateTime.now()));

            // Set request and response details
            interfaceLoggingRequest.setRequest(mapper.writeValueAsString(request));
            interfaceLoggingRequest.setResponse(mapper.writeValueAsString(response));

            // Set status and correlation ID
            interfaceLoggingRequest.setStatus(InterfaceLoggingConstants.SUCCESS);
            interfaceLoggingRequest.setCorrelationId((String) httpServletRequest.getAttribute(TransactionUtilityConstants.CORRELATION_ID));

            // Populate null strings
            interfaceLoggingRequest = populateNullString(interfaceLoggingRequest);

            // Log the successful transaction
            log.info("Logging successful interface transaction with Correlation ID: {}", interfaceLoggingRequest.getCorrelationId());
            interfaceLoggingService.log(interfaceLoggingRequest);
            log.info("Successful interface transaction logged: {}", interfaceLoggingRequest);
        } catch (Exception e) {
            log.error("Failed to log successful interface transaction: {}", e.getMessage(), e);
        }
    }

    /**
     * Populates null strings in the InterfaceLoggingRequest object.
     *
     * @param request the interface logging request object
     * @return the populated interface logging request object
     */
    private InterfaceLoggingRequest populateNullString(InterfaceLoggingRequest request) {
        // Implementation of populateNullString to handle null values
        // ...
        return request;
    }
}






