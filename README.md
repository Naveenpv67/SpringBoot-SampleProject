public class DematOBPConstants {
    public static final String SOAP_ENVELOPE_START = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">";
    public static final String SOAP_HEADER = "<SOAP-ENV:Header>";
    public static final String SOAP_BODY_START = "<SOAP-ENV:Body>";
    public static final String DEMAT_LANDING_PAGE_START = "<dematLandingPage>";
    public static final String ARG_START = "<arg>";
    public static final String BANK_CODE_START = "<con:bankCode>";
    public static final String BANK_CODE_END = "</con:bankCode>";
    public static final String CHANNEL_START = "<con:channel>";
    public static final String CHANNEL_END = "</con:channel>";
    public static final String TRANSACTING_PARTY_CODE_START = "<con:transactingPartyCode>";
    public static final String TRANSACTING_PARTY_CODE_END = "</con:transactingPartyCode>";
    public static final String TRANSACTION_BRANCH_START = "<con:transactionBranch>";
    public static final String TRANSACTION_BRANCH_END = "</con:transactionBranch>";
    public static final String USER_ID_START = "<con:userId>";
    public static final String USER_ID_END = "</con:userId>";
    public static final String EXTERNAL_REFERENCE_NO_START = "<con:externalReferenceNo>";
    public static final String EXTERNAL_REFERENCE_NO_END = "</con:externalReferenceNo>";
    public static final String ARG_END = "</arg>";
    public static final String ARG1_START = "<arg1>";
    public static final String MSGHDR_START = "<msgHdr>";
    public static final String MSG_TP_START = "<msg.tp>";
    public static final String MSG_TP_END = "</msg.tp>";
    public static final String REQAPP_START = "<req.app>";
    public static final String REQAPP_END = "</req.app>";
    public static final String REQTMSTMP_START = "<req.tmstmp>";
    public static final String REQTMSTMP_END = "</req.tmstmp>";
    public static final String MSGHDR_END = "</msgHdr>";
    public static final String REQDTLS_START = "<reqDtls>";
    public static final String CUSTID_START = "<custid>";
    public static final String CUSTID_END = "</custid>";
    public static final String INCLPLGBAL_START = "<incl.plgbal>";
    public static final String INCLPLGBAL_END = "</incl.plgbal>";
    public static final String INCLPVAL_START = "<incl.pval>";
    public static final String INCLPVAL_END = "</incl.pval>";
    public static final String INCLPVANAL_START = "<incl.pvanal>";
    public static final String INCLPVANAL_END = "</incl.pvanal>";
    public static final String REGREFNO_START = "<regrefno>";
    public static final String REGREFNO_END = "</regrefno>";
    public static final String REQDTLS_END = "</reqDtls>";
    public static final String ARG1_END = "</arg1>";
    public static final String DEMAT_LANDING_PAGE_END = "</dematLandingPage>";
    public static final String SOAP_BODY_END = "</SOAP-ENV:Body>";
    public static final String SOAP_ENVELOPE_END = "</SOAP-ENV:Envelope>";

    // Existing constants
    public static final String OBP_DEMAT_LANDING_PAGE_BANK_CODE = "YourBankCode";
    public static final String OBP_DEMAT_LANDING_PAGE_CHANNEL = "YourChannel";
    public static final String OBP_DEMAT_LANDING_PAGE_TX_PARTY_CD = "YourTxPartyCode";
    public static final String OBP_DEMAT_LANDING_PAGE_TX_BRANCH = "YourTxBranch";
    public static final String OBP_DEMAT_LANDING_PAGE_USER_ID = "YourUserId";
    public static final String OBP_DEMAT_LANDING_PAGE_MSG_TP = "YourMsgTp";
    public static final String OBP_DEMAT_LANDING_PAGE_REQAPP = "YourReqApp";
    public static final String OBP_DEMAT_LANDING_PAGE_REQTMSTMP = "YourReqTmsTmp";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPLGBAL = "YourInclPlgBal";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPVAL = "YourInclPval";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPVANAL = "YourInclPvanal";
}


StringBuilder soapEnvelope = null;

try {
    String userId;
    String customerId;
    String refNo = String.valueOf((new Random()).nextInt(9000000) + 1000000);

    // Use StringBuilder to build the XML structure using constants
    soapEnvelope = new StringBuilder();

    soapEnvelope.append(DematOBPConstants.SOAP_ENVELOPE_START)
                .append(DematOBPConstants.SOAP_HEADER)
                .append(DematOBPConstants.SOAP_BODY_START)
                .append(DematOBPConstants.DEMAT_LANDING_PAGE_START)
                .append(DematOBPConstants.ARG_START)
                .append(DematOBPConstants.BANK_CODE_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_BANK_CODE)
                .append(DematOBPConstants.BANK_CODE_END)
                .append(DematOBPConstants.CHANNEL_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_CHANNEL)
                .append(DematOBPConstants.CHANNEL_END)
                .append(DematOBPConstants.TRANSACTING_PARTY_CODE_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_TX_PARTY_CD)
                .append(DematOBPConstants.TRANSACTING_PARTY_CODE_END)
                .append(DematOBPConstants.TRANSACTION_BRANCH_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_TX_BRANCH)
                .append(DematOBPConstants.TRANSACTION_BRANCH_END)
                .append(DematOBPConstants.USER_ID_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_USER_ID)
                .append(DematOBPConstants.USER_ID_END)
                .append(DematOBPConstants.EXTERNAL_REFERENCE_NO_START)
                .append(refNo)
                .append(DematOBPConstants.EXTERNAL_REFERENCE_NO_END)
                .append(DematOBPConstants.ARG_END)
                .append(DematOBPConstants.ARG1_START)
                .append(DematOBPConstants.MSGHDR_START)
                .append(DematOBPConstants.MSG_TP_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_MSG_TP)
                .append(DematOBPConstants.MSG_TP_END)
                .append(DematOBPConstants.REQAPP_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_REQAPP)
                .append(DematOBPConstants.REQAPP_END)
                .append(DematOBPConstants.REQTMSTMP_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_REQTMSTMP)
                .append(DematOBPConstants.REQTMSTMP_END)
                .append(DematOBPConstants.MSGHDR_END)
                .append(DematOBPConstants.REQDTLS_START)
                .append(DematOBPConstants.CUSTID_START)
                .append(userId)
                .append(DematOBPConstants.CUSTID_END)
                .append(DematOBPConstants.INCLPLGBAL_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPLGBAL)
                .append(DematOBPConstants.INCLPLGBAL_END)
                .append(DematOBPConstants.INCLPVAL_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPVAL)
                .append(DematOBPConstants.INCLPVAL_END)
                .append(DematOBPConstants.INCLPVANAL_START)
                .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPVANAL)
                .append(DematOBPConstants.INCLPVANAL_END)
                .append(DematOBPConstants.REGREFNO_START)
                .append(refNo)
                .append(DematOBPConstants.REGREFNO_END)
                .append(DematOBPConstants.REQDTLS_END)
                .append(DematOBPConstants.ARG1_END)
                .append(DematOBPConstants.DEMAT_LANDING_PAGE_END)
                .append(DematOBPConstants.SOAP_BODY_END)
                .append(DematOBPConstants.SOAP_ENVELOPE_END);
} catch (JSONException je) {
    return soapEnvelope.toString();
}

