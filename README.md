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
        String devicePlatform = request.getHeader("Device-Platform");
        String deviceOsVersion = request.getHeader("Device-OS-Version");

        // Proceed with the method execution
        Object methodResult = null;
        try {
            // Execute the controller method
            methodResult = joinPoint.proceed();
        } catch (Exception e) {
            // Log failure transaction for exceptions
            transactionLoggingUtil.logFailedTransaction(requestBody, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), e, request, response);
            // Re-throw the exception to propagate it up the call stack
            throw e;
        }
        
        // Log transaction
        if (response != null) {
            String responseBody = extractResponseBody(response);
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                // Log successful transaction
                TransactionLoggingRequest transactionLoggingRequest = transactionLoggingUtil.createTransactionLoggingRequest(new TransactionEventPayload(CommonUtilityFunctions.getRandomUUID(), "LOGIN"));
                transactionLoggingUtil.logSuccessfulTransaction(requestBody, responseBody, transactionLoggingRequest, request, response);
            } else {
                // Log failure transaction for non-2xx status codes
                transactionLoggingUtil.logFailedTransaction(requestBody, String.valueOf(response.getStatus()), "Error Message", null, request, response);
            }
        } else {
            // Log failure transaction when there's no response
            transactionLoggingUtil.logFailedTransaction(requestBody, HttpStatus.INTERNAL_SERVER_ERROR.toString(), "No response received", null, request, response);
        }

        return methodResult;
    }

    // Method to extract request body as string
    private String extractRequestBody(HttpServletRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            // Log error if unable to extract request body
            e.printStackTrace();
            return null;
        }
    }

    // Method to extract response body as string
    private String extractResponseBody(HttpServletResponse response) {
        try {
            return objectMapper.writeValueAsString(response.getBufferedReader().lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            // Log error if unable to extract response body
            e.printStackTrace();
            return null;
        }
    }
}
