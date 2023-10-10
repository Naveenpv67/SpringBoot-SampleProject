@Test
@DisplayName("Validate Blank Bank Code")
public void testBankCodeBlankValidation() throws Exception {
    validRequest.getBankDetailsDTO().setBankCode("");
    performValidationAndExpectError("bankCode", validRequest, ErrorCode.V0001);
}

@Test
@DisplayName("Validate Null Bank Code")
public void testBankCodeNullValidation() throws Exception {
    validRequest.getBankDetailsDTO().setBankCode(null);
    performValidationAndExpectError("bankCode", validRequest, ErrorCode.V0001);
}

@Test
@DisplayName("Validate Special Characters in Bank Code")
public void testBankCodeSpecialCharacterValidation() throws Exception {
    validRequest.getBankDetailsDTO().setBankCode("!@#");
    performValidationAndExpectError("bankCode", validRequest, ErrorCode.Ve002);
}

@Test
@DisplayName("Validate Alphanumeric Bank Code")
public void testBankCodeAlphanumericValidation() throws Exception {
    validRequest.getBankDetailsDTO().setBankCode("alpha123");
    performValidationAndExpectError("bankCode", validRequest, ErrorCode.Ve004);
}

@Test
@DisplayName("Validate Missing Bank Code")
public void testBankCodeMissingValidation() throws Exception {
    validRequest.getBankDetailsDTO().setBankCode(null);
    performValidationAndExpectError("bankCode", validRequest, ErrorCode.V0001);
}
