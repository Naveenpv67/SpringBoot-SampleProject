private DebitCardHotlistingRequestDTO createValidDebitCardHotlistingRequestDTO() {
    DebitCardHotlistingRequestDTO debitCardHotlistingRequestDTO = new DebitCardHotlistingRequestDTO();
    debitCardHotlistingRequestDTO.setRequestString(createValidRequestString());
    return debitCardHotlistingRequestDTO;
}

private DebitCardHotlistingRequestString createValidRequestString() {
    DebitCardHotlistingRequestString requestString = new DebitCardHotlistingRequestString();
    DCMSServices dcmServices = new DCMSServices();
    
    // Set values for the Header
    Header header = new Header();
    header.setVersion("1.0");
    header.setSrvType("TRANS");
    header.setSryName("HOT CARD");
    header.setSrcApp("FLEX@");
    header.setTargetApp("DCMS");
    header.setTimestamp("26-07-2021 13:06:23");
    header.setSrcMsgId(4022);
    header.setOrgID("HDFC");
    
    // Set values for the Body
    Body body = new Body();
    SrvReg srvReg = new SrvReg();
    
    // Set values for CardHotlistReq
    CardHotlistReq cardHotlistReq = new CardHotlistReq();
    cardHotlistReq.setCardNo(4386243406028003L);
    cardHotlistReq.setReason("L");
    cardHotlistReq.setRemarks("Bb test");
    
    srvReg.setCardHotlistReq(cardHotlistReq);
    body.setSrxReg(srvReg);
    dcmServices.setHeader(header);
    dcmServices.setBody(body);
    requestString.setDCMSServices(dcmServices);
    
    return requestString;
}
