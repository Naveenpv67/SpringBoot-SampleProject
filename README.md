import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test class for validating fields in the 'Header' object of the Debit Card Hotlisting Request.
 */
public class HeaderValidationTest {
    private DebitCardHotlistingRequestDTO validRequest;

    /**
     * Setup method to initialize a valid request object before each test.
     */
    @BeforeEach
    public void setup() {
        validRequest = createValidDebitCardHotlistingRequestDTO();
    }

    /**
     * Test method to validate that the 'Version' field does not allow a blank value.
     */
    @Test
    @DisplayName("Validate Blank Version")
    public void testHeaderVersionBlankValidation() {
        setHeaderFieldValue("Version", "");
        performValidationAndExpectError("Version", validRequest, ErrorCode.V0001);
    }

    /**
     * Test method to validate that the 'Version' field does not allow a null value.
     */
    @Test
    @DisplayName("Validate Null Version")
    public void testHeaderVersionNullValidation() {
        setHeaderFieldValue("Version", null);
        performValidationAndExpectError("Version", validRequest, ErrorCode.V0001);
    }

    /**
     * Test method to validate that the 'Version' field allows a valid value.
     */
    @Test
    @DisplayName("Add Valid Version")
    public void testHeaderVersionValidValidation() {
        setHeaderFieldValue("Version", "1.0");
        performValidationAndExpectNoError(validRequest);
    }

    /**
     * Test method to validate that the 'Version' field does not allow a value with excessive length.
     */
    @Test
    @DisplayName("Validate Version Length")
    public void testHeaderVersionLengthValidation() {
        setHeaderFieldValue("Version", "1234567890");
        performValidationAndExpectError("Version", validRequest, ErrorCode.V000X);
    }

    /**
     * Test method to validate that the 'Version' field does not allow special characters.
     */
    @Test
    @DisplayName("Add Valid Version with Special Characters")
    public void testHeaderVersionSpecialCharsValidation() {
        setHeaderFieldValue("Version", "1.0@");
        performValidationAndExpectError("Version", validRequest, ErrorCode.V000Y);
    }

    /**
     * Test method to validate that the 'Version' field allows alphanumeric characters.
     */
    @Test
    @DisplayName("Add Valid Version with Alphanumeric Characters")
    public void testHeaderVersionAlphanumericValidation() {
        setHeaderFieldValue("Version", "V1ersion1");
        performValidationAndExpectNoError(validRequest);
    }

    // Add similar methods for other fields in the Header object (SrvType, SryName, SrcApp, TargetApp, Timestamp, SrcMsgId, OrgID).

    /**
     * Method to set a specific field value in the Header object.
     *
     * @param fieldName The name of the field to set.
     * @param value     The value to set for the field.
     */
    private void setHeaderFieldValue(String fieldName, String value) {
        Header header = validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader();

        switch (fieldName) {
            case "Version":
                header.setVersion(value);
                break;
            case "SrvType":
                header.setSrvType(value);
                break;
            case "SryName":
                header.setSryName(value);
                break;
            case "SrcApp":
                header.setSrcApp(value);
                break;
            case "TargetApp":
                header.setTargetApp(value);
                break;
            case "Timestamp":
                header.setTimestamp(value);
                break;
            case "SrcMsgId":
                header.setSrcMsgId(Integer.parseInt(value));
                break;
            case "OrgID":
                header.setOrgID(value);
                break;
        }
    }
}
