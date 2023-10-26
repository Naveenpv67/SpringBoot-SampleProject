// For the 'Version' field
@Test
@DisplayName("Validate Blank Version")
public void testHeaderVersionBlankValidation() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader().setVersion("");
    performValidationAndExpectError("Version", validRequest, ErrorCode.V0001);
}

@Test
@DisplayName("Validate Null Version")
public void testHeaderVersionNullValidation() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader().setVersion(null);
    performValidationAndExpectError("Version", validRequest, ErrorCode.V0001);
}

@Test
@DisplayName("Add Valid Version")
public void testHeaderVersionValidValidation() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader().setVersion("1.0");
    performValidationAndExpectNoError(validRequest);
}

@Test
@DisplayName("Validate Version Length")
public void testHeaderVersionLengthValidation() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader().setVersion("1234567890");
    performValidationAndExpectError("Version", validRequest, ErrorCode.V000X);
}

@Test
@DisplayName("Add Valid Version with Special Characters")
public void testHeaderVersionSpecialCharsValidation() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader().setVersion("1.0@");
    performValidationAndExpectError("Version", validRequest, ErrorCode.V000Y);
}

@Test
@DisplayName("Add Valid Version with Alphanumeric Characters")
public void testHeaderVersionAlphanumericValidation() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getHeader().setVersion("V1ersion1");
    performValidationAndExpectNoError(validRequest);
}

// Repeat the above structure for the rest of the fields in the 'Header' object with their respective validations.
