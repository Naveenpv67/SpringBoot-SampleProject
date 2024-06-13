public class DematOBPConstants {
    public static final String SOAP_ENVELOPE_START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                                                     "xmlns:ing=\"http://inquiry.service.demat.appx.cz.fc.ofss.com/\" " +
                                                     "xmlns:con=\"http://context.app.fc.ofss.com\" " +
                                                     "xmlns:exc=\"http://exception.infra.fc.ofss.com\" " +
                                                     "xmlns:dat=\"http://datatype.fc.ofss.com\" " +
                                                     "xmlns:dto=\"http://dto.common.domain.framework.fc.ofss.com\" " +
                                                     "xmlns:dom=\"http://domain.framework.fc.ofss.com\">";
    public static final String SOAP_HEADER = "<soapenv:Header/>";
    public static final String SOAP_BODY_START = "<soapenv:Body>";
    public static final String DO_VIEW_HOLDING_STATEMENT_START = "<ing:doViewHoldingStatement>";
    public static final String ARG_START = "<arg0>";
    public static final String ARG_END = "</arg0>";
    public static final String ARG1_START = "<arg1>";
    public static final String ARG1_END = "</arg1>";
    public static final String MSGHDR_START = "<msghdr>";
    public static final String MSGHDR_END = "</msghdr>";
    public static final String REQDTLS_START = "<reqdtls>";
    public static final String REQDTLS_END = "</reqdtls>";
    public static final String SOAP_BODY_END = "</soapenv:Body>";
    public static final String SOAP_ENVELOPE_END = "</soapenv:Envelope>";
    
    public static final String VIEW_HOLDING_BANK_CODE = "08";
    public static final String VIEW_HOLDING_CHANNEL = "BB54";
    public static final String VIEW_HOLDING_TX_PARTY_CODE = "50236383";
    public static final String VIEW_HOLDING_TX_BRANCH = "089999";
    public static final String VIEW_HOLDING_USER_ID = "DevUser01";
    public static final String VIEW_HOLDING_MSG_TP = "DBHLDBALRQ";
    public static final String VIEW_HOLDING_REQAPP = "BACKBASE";
    public static final String VIEW_HOLDING_REQTMSTTMP = "20170613101010";
    public static final String VIEW_HOLDING_ACCT_NO = "16495657";
    public static final String VIEW_HOLDING_DP_ID = "IN301549";
    public static final String VIEW_HOLDING_REG_REF_NO = "234225783";
    
    public static final String BANK_CODE_TAG_START = "<con:bankCode>";
    public static final String BANK_CODE_TAG_END = "</con:bankCode>";
    public static final String CHANNEL_TAG_START = "<con:channel>";
    public static final String CHANNEL_TAG_END = "</con:channel>";
    public static final String TRANS_PARTY_CODE_TAG_START = "<con:transactingPartyCode>";
    public static final String TRANS_PARTY_CODE_TAG_END = "</con:transactingPartyCode>";
    public static final String TX_BRANCH_TAG_START = "<con:transactionBranch>";
    public static final String TX_BRANCH_TAG_END = "</con:transactionBranch>";
    public static final String USER_ID_TAG_START = "<con:userId>";
    public static final String USER_ID_TAG_END = "</con:userId>";
    public static final String EXT_REF_NO_TAG_START = "<con:externalReferenceNo>";
    public static final String EXT_REF_NO_TAG_END = "</con:externalReferenceNo>";
    
    public static final String MSG_TP_TAG_START = "<msgt.p>";
    public static final String MSG_TP_TAG_END = "</msgt.p>";
    public static final String REQAPP_TAG_START = "<reqapp>";
    public static final String REQAPP_TAG_END = "</reqapp>";
    public static final String REQTMS_TMP_TAG_START = "<reqtms.tmp>";
    public static final String REQTMS_TMP_TAG_END = "</reqtms.tmp>";
    
    public static final String ACCT_NO_TAG_START = "<acct.no>";
    public static final String ACCT_NO_TAG_END = "</acct.no>";
    public static final String DP_ID_TAG_START = "<dp.id>";
    public static final String DP_ID_TAG_END = "</dp.id>";
    public static final String REG_REF_NO_TAG_START = "<regrefno>";
    public static final String REG_REF_NO_TAG_END = "</regrefno>";
    
    public static final String DO_VIEW_HOLDING_STATEMENT_END = "</ing:doViewHoldingStatement>";
}



StringBuilder soapEnvelope = null;
try {
    String userId = DematOBPConstants.VIEW_HOLDING_USER_ID;  // Or retrieve it from your context
    String refNo = String.valueOf((new Random()).nextInt(9000000) + 1000000);

    soapEnvelope = new StringBuilder();
    soapEnvelope.append(DematOBPConstants.SOAP_ENVELOPE_START)
                .append(DematOBPConstants.SOAP_HEADER)
                .append(DematOBPConstants.SOAP_BODY_START)
                .append(DematOBPConstants.DO_VIEW_HOLDING_STATEMENT_START)
                .append(DematOBPConstants.ARG_START)
                .append(DematOBPConstants.BANK_CODE_TAG_START).append(DematOBPConstants.VIEW_HOLDING_BANK_CODE).append(DematOBPConstants.BANK_CODE_TAG_END)
                .append(DematOBPConstants.CHANNEL_TAG_START).append(DematOBPConstants.VIEW_HOLDING_CHANNEL).append(DematOBPConstants.CHANNEL_TAG_END)
                .append(DematOBPConstants.TRANS_PARTY_CODE_TAG_START).append(DematOBPConstants.VIEW_HOLDING_TX_PARTY_CODE).append(DematOBPConstants.TRANS_PARTY_CODE_TAG_END)
                .append(DematOBPConstants.TX_BRANCH_TAG_START).append(DematOBPConstants.VIEW_HOLDING_TX_BRANCH).append(DematOBPConstants.TX_BRANCH_TAG_END)
                .append(DematOBPConstants.USER_ID_TAG_START).append(userId).append(DematOBPConstants.USER_ID_TAG_END)
                .append(DematOBPConstants.EXT_REF_NO_TAG_START).append(refNo).append(DematOBPConstants.EXT_REF_NO_TAG_END)
                .append(DematOBPConstants.ARG_END)
                .append(DematOBPConstants.ARG1_START)
                .append(DematOBPConstants.MSGHDR_START)
                .append(DematOBPConstants.MSG_TP_TAG_START).append(DematOBPConstants.VIEW_HOLDING_MSG_TP).append(DematOBPConstants.MSG_TP_TAG_END)
                .append(DematOBPConstants.REQAPP_TAG_START).append(DematOBPConstants.VIEW_HOLDING_REQAPP).append(DematOBPConstants.REQAPP_TAG_END)
                .append(DematOBPConstants.REQTMS_TMP_TAG_START).append(DematOBPConstants.VIEW_HOLDING_REQTMSTTMP).append(DematOBPConstants.REQTMS_TMP_TAG_END)
                .append(DematOBPConstants.MSGHDR_END)
                .append(DematOBPConstants.REQDTLS_START)
                .append(DematOBPConstants.ACCT_NO_TAG_START).append(DematOBPConstants.VIEW_HOLDING_ACCT_NO).append(DematOBPConstants.ACCT_NO_TAG_END)
                .append(DematOBPConstants.DP_ID_TAG_START).append(DematOBPConstants.VIEW_HOLDING_DP_ID).append(DematOBPConstants.DP_ID_TAG_END)
                .append(DematOBPConstants.REG_REF_NO_TAG_START).append(DematOBPConstants.VIEW_HOLDING_REG_REF_NO).append(DematOBPConstants.REG_REF_NO_TAG_END)
                .append(DematOBPConstants.REQDTLS_END)
                .append(DematOBPConstants.ARG1_END)
                .append(DematOBPConstants.DO_VIEW_HOLDING_STATEMENT_END)
                .append(DematOBPConstants.SOAP_BODY_END)
                .append(DematOBPConstants.SOAP_ENVELOPE_END);
} catch (Exception e) {
    e.printStackTrace();
}
