public class CustomValidationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String fieldName;

    public CustomValidationException(ErrorCode errorCode, String fieldName) {
        super(errorCode.getMessage(fieldName));
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getFieldName() {
        return fieldName;
    }
}






public enum ErrorCode {
    V0001("Field '{}' can't be blank or null"),
    V0002("Field '{}' can't contain special characters"),
    V0003("Field '{}' is invalid"),
    V0004("Field '{}' is missing"),
    V0005("Field '{}' can't contain alphanumeric characters");

    private final String messageTemplate;

    ErrorCode(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessage(String fieldName) {
        return messageTemplate.replace("{}", fieldName);
    }
}




@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(CustomValidationException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse errorResponse = new ErrorResponse(
            "error", // Set the 'type' as 'error' or any other relevant value
            errorCode.name(), // Use the ErrorCode as the 'code'
            ex.getMessage(), // Use the exception message directly as the 'errorDescription'
            ex.getMessage() // Set the 'devError' as the exception message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}




@RestController
public class YourController {

    @PostMapping("/view-recent-transactions")
    public ResponseEntity<Page<Transactions>> viewRecentTransactions(@RequestBody ViewRecentTransactionsDTO viewRecentTransactionsDTO) {
        validateBlankOrNull("bankCode", viewRecentTransactionsDTO.getBankCode());
        validateSpecialCharacters("bankCode", viewRecentTransactionsDTO.getBankCode());
        validateInvalid("bankCode", viewRecentTransactionsDTO.getBankCode());
        validateMissing("bankCode", viewRecentTransactionsDTO.getBankCode());
        validateAlphanumeric("bankCode", viewRecentTransactionsDTO.getBankCode());

        // Other field validations

        // If all validations pass, proceed with your logic

        // Return the response
    }

    // Generic validation methods

    private void validateBlankOrNull(String fieldName, String fieldValue) {
        if (StringUtils.isBlank(fieldValue)) {
            throw new CustomValidationException(ErrorCode.V0001, fieldName);
        }
    }

    private void validateSpecialCharacters(String fieldName, String fieldValue) {
        if (fieldValue != null && fieldValue.matches(".*[!@#$%^&*()].*")) {
            throw new CustomValidationException(ErrorCode.V0002, fieldName);
        }
    }

    private void validateInvalid(String fieldName, String fieldValue) {
        // Add logic to check for invalid values and throw CustomValidationException if found
    }

    private void validateMissing(String fieldName, String fieldValue) {
        if (fieldValue == null) {
            throw new CustomValidationException(ErrorCode.V0004, fieldName);
        }
    }

    private void validateAlphanumeric(String fieldName, String fieldValue) {
        if (fieldValue != null && !fieldValue.matches("^[a-zA-Z0-9]*$")) {
            throw new CustomValidationException(ErrorCode.V0005, fieldName);
        }
    }
}




@RunWith(SpringRunner.class)
@WebMvcTest(YourController.class)
public class YourControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private ViewRecentTransactionsDTO validRequest;

    @BeforeEach
    public void setup() {
        // Initialize a valid request with common valid data
        validRequest = createValidRequest();
    }

    @Test
    public void testBlankValidation() throws Exception {
        performValidationAndExpectError("bankCode", "", ErrorCode.V0001);
    }

    @Test
    public void testNullValidation() throws Exception {
        validRequest.getBankDetailsDTO().setChannel(null);
        performValidationAndExpectError("channel", null, ErrorCode.V0001);
    }

    @Test
    public void testSpecialCharacterValidation() throws Exception {
        performValidationAndExpectError("bankCode", "!@#", ErrorCode.V0002);
    }

    @Test
    public void testInvalidValidation() throws Exception {
        performValidationAndExpectError("bankCode", "invalidValue", ErrorCode.V0003);
    }

    @Test
    public void testMissingValidation() throws Exception {
        validRequest.getBankDetailsDTO().setTransactionBranch(null);
        performValidationAndExpectError("transactionBranch", null, ErrorCode.V0004);
    }

    @Test
    public void testAlphanumericValidation() throws Exception {
        performValidationAndExpectError("bankCode", "alpha123", ErrorCode.V0005);
    }

    // Helper methods for creating and performing validations
    private ViewRecentTransactionsDTO createValidRequest() {
        ViewRecentTransactionsDTO request = new ViewRecentTransactionsDTO();
        request.setBankDetailsDTO(createValidBankDetailsDTO());
        request.setAccountStatementDTO(createValidAccountStatementDTO());
        return request;
    }

    private BankDetailsDTO createValidBankDetailsDTO() {
        BankDetailsDTO bankDetailsDTO = new BankDetailsDTO();
        bankDetailsDTO.setBankCode("08");
        bankDetailsDTO.setChannel("IB01");
        bankDetailsDTO.setUserid("DevUser01");
        bankDetailsDTO.setTransactionBranch("089999");
        bankDetailsDTO.setExternalReferenceNo("1245786569");
        bankDetailsDTO.setTransactingPartyCode("50000913");
        bankDetailsDTO.setServiceCode("CHAPI10906");
        bankDetailsDTO.setUserReferenceNumber("BALINQ001");
        return bankDetailsDTO;
    }

    private void performValidationAndExpectError(String fieldName, String fieldValue, ErrorCode errorCode) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.putPOJO(fieldName, fieldValue);

        mockMvc.perform(post("/view-recent-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("error")))
                .andExpect(jsonPath("$.code", is(errorCode.name())))
                .andExpect(jsonPath("$.errorDescription", is(errorCode.getMessage(fieldName))));
    }
}

