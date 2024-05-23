import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InterfaceLoggingComponent {

    @Value("${Enable12000Utility:false}")
    private boolean enable12000Utility;

    private static final Logger log = LoggerFactory.getLogger(InterfaceLoggingComponent.class);

    @Autowired
    private InterfaceLoggingUtils interfaceLoggingUtils;

    @Autowired
    private HttpServletRequest request;

    /**
     * Utility method to check if the logging is enabled.
     *
     * @return true if logging is enabled, false otherwise
     */
    private boolean isLoggingEnabled() {
        return enable12000Utility;
    }

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
        if (!isLoggingEnabled()) {
            log.info("Interface logging is disabled");
            return;
        }

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
