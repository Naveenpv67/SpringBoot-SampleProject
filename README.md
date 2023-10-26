@Test
@DisplayName("Validate Null DebitCardHotlistingRequestDTO")
public void testBlankValidation1() throws Exception {
    validRequest.setDebitCardHotlistingRequestDTO(null);
    performValidationAndExpectError("DebitCardHotlistingRequestDTO", validRequest, ErrorCode.V0009);
}

@Test
@DisplayName("Validate Null RequestString")
public void testBlankValidation2() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().setRequestString(null);
    performValidationAndExpectError("RequestString", validRequest, ErrorCode.V0009);
}

@Test
@DisplayName("Validate Null DCMSServices")
public void testBlankValidation3() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().setDCMSServices(null);
    performValidationAndExpectError("DCMSServices", validRequest, ErrorCode.V0009);
}

@Test
@DisplayName("Validate Null Header")
public void testBlankValidation4() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().setHeader(null);
    performValidationAndExpectError("Header", validRequest, ErrorCode.V0009);
}

@Test
@DisplayName("Validate Null Body")
public void testBlankValidation5() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().setBody(null);
    performValidationAndExpectError("Body", validRequest, ErrorCode.V0009);
}

@Test
@DisplayName("Validate Null SrvReg")
public void testBlankValidation6() throws Exception {
    validRequest.getDebitCardHotlistingRequestDTO().getRequestString().getDCMSServices().getBody().setSrxReg(null);
    performValidationAndExpectError("SrvReg", validRequest, ErrorCode.V0009);
}
