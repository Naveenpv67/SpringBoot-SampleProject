@Component
public class TransactionLoggingUtil {

    @Autowired
    private ObjectMapper objectMapper;

    // Other methods...

    // Method to populate fields of TransactionLoggingRequest object
    public void populateTransactionLoggingRequest(TransactionLoggingRequest transactionLoggingRequest, HttpServletRequest request) {
        // Set device information from request headers
        transactionLoggingRequest.setDeviceOsPlatform(request.getHeader("Device-Platform"));
        transactionLoggingRequest.setDeviceOsVersion(request.getHeader("Device-OS-Version"));
        
        // Set other fields as needed
        transactionLoggingRequest.setTimestamp(new Date());
        // You can populate more fields here if needed
    }
}

@Aspect
@Component
public class ControllerLoggingAspect {

    @Autowired
    private TransactionLoggingUtil transactionLoggingUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.yourpackage.controller.*.*(..))")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get HTTP servlet request and response
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        // Check if request method is GET
        if (request.getMethod().equals("GET")) {
            return joinPoint.proceed(); // Skip logging for GET requests
        }

        // Get request details
        String requestBody = extractRequestBody(request);

        // Populate transaction logging request
        TransactionLoggingRequest transactionLoggingRequest = transactionLoggingUtil.createTransactionLoggingRequest(new TransactionEventPayload(CommonUtilityFunctions.getRandomUUID(), "LOGIN"));
        transactionLoggingUtil.populateTransactionLoggingRequest(transactionLoggingRequest, request);
        
        // Proceed with the method execution
        Object methodResult = null;
        try {
            // Execute the controller method
            methodResult = joinPoint.proceed();
        } catch (Exception e) {
            // Log failure transaction for exceptions
            transactionLoggingUtil.logFailedTransaction(requestBody, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), e, transactionLoggingRequest, request, response);
            // Re-throw the exception to propagate it up the call stack
            throw e;
        }
        
        // Log transaction
        if (response != null) {
            String responseBody = extractResponseBody(response);
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                // Log successful transaction
                transactionLoggingUtil.logSuccessfulTransaction(requestBody, responseBody, transactionLoggingRequest, request, response);
            } else {
                // Log failure transaction for non-2xx status codes
                transactionLoggingUtil.logFailedTransaction(requestBody, String.valueOf(response.getStatus()), "Error Message", null, transactionLoggingRequest, request, response);
            }
        } else {
            // Log failure transaction when there's no response
            transactionLoggingUtil.logFailedTransaction(requestBody, HttpStatus.INTERNAL_SERVER_ERROR.toString(), "No response received", null, transactionLoggingRequest, request, response);
        }

        return methodResult;
    }

    // Other methods...
}
