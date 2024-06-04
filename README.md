public class DematOBPConstants {
    public static final String SOAP_ENVELOPE_START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ing=\"http://inquiry.service.demat.appx.cz.fc.ofss.com/\" xmlns:con=\"http://context.app.fc.ofss.com\" xmlns:exc=\"http://exception.infra.fc.ofss.com\" xmlns:dat=\"http://datatype.fc.ofss.com\" xmlns:dto=\"http://dto.common.domain.framework.fc.ofss.com\" xmlns:dom=\"http://domain.framework.fc.ofss.com\">";
    public static final String SOAP_HEADER = "<soapenv:Header/>";
    public static final String SOAP_BODY_START = "<soapenv:Body>";
    public static final String SOAP_BODY_END = "</soapenv:Body>";
    public static final String SOAP_ENVELOPE_END = "</soapenv:Envelope>";
    public static final String DO_DEMAT_LANDING_PAGE_START = "<ing:doDematLandingPage>";
    public static final String DO_DEMAT_LANDING_PAGE_END = "</ing:doDematLandingPage>";
    public static final String ARG_START = "<arg>";
    public static final String ARG_END = "</arg>";
    public static final String ARG1_START = "<arg1>";
    public static final String ARG1_END = "</arg1>";
    public static final String MSGHDR_START = "<msghdr>";
    public static final String MSGHDR_END = "</msghdr>";
    public static final String REGDTLS_START = "<regdtls>";
    public static final String REGDTLS_END = "</regdtls>";

    // Placeholder constants for various dynamic parts
    public static final String OBP_DEMAT_LANDING_PAGE_BANK_CODE_START = "<con:bankCode>";
    public static final String OBP_DEMAT_LANDING_PAGE_BANK_CODE_END = "</con:bankCode>";
    public static final String OBP_DEMAT_LANDING_PAGE_CHANNEL_START = "<con:channel>";
    public static final String OBP_DEMAT_LANDING_PAGE_CHANNEL_END = "</con:channel>";
    public static final String OBP_DEMAT_LANDING_PAGE_TX_PARTY_CD_START = "<con:transactingPartyCode>";
    public static final String OBP_DEMAT_LANDING_PAGE_TX_PARTY_CD_END = "</con:transactingPartyCode>";
    public static final String OBP_DEMAT_LANDING_PAGE_TX_BRANCH_START = "<con:transactionBranch>";
    public static final String OBP_DEMAT_LANDING_PAGE_TX_BRANCH_END = "</con:transactionBranch>";
    public static final String OBP_DEMAT_LANDING_PAGE_USER_ID_START = "<con:userId>";
    public static final String OBP_DEMAT_LANDING_PAGE_USER_ID_END = "</con:userId>";
    public static final String OBP_DEMAT_LANDING_PAGE_MSG_TP_START = "<msgtp>";
    public static final String OBP_DEMAT_LANDING_PAGE_MSG_TP_END = "</msgtp>";
    public static final String OBP_DEMAT_LANDING_PAGE_REQAPP_START = "<reqapp>";
    public static final String OBP_DEMAT_LANDING_PAGE_REQAPP_END = "</reqapp>";
    public static final String OBP_DEMAT_LANDING_PAGE_REQTMSTTMP_START = "<reqtmstmp>";
    public static final String OBP_DEMAT_LANDING_PAGE_REQTMSTTMP_END = "</reqtmstmp>";
    public static final String OBP_DEMAT_LANDING_PAGE_INCL_PLGBAL_START = "<inclplgbal>";
    public static final String OBP_DEMAT_LANDING_PAGE_INCL_PLGBAL_END = "</inclplgbal>";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPVAL_START = "<inclpval>";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPVAL_END = "</inclpval>";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPVANAL_START = "<inclpyanal>";
    public static final String OBP_DEMAT_LANDING_PAGE_INCLPVANAL_END = "</inclpyanal>";
}



import org.json.JSONObject;
import org.json.JSONException;

public class JsonToSoapXmlConverter {
    public static void main(String[] args) {
        // Example JSON input
        String jsonString = "{"
                + "\"bankCode\": \"08\","
                + "\"channel\": \"BB53\","
                + "\"transactingPartyCode\": \"50000011\","
                + "\"transactionBranch\": \"089999\","
                + "\"userId\": \"IB01_USER\","
                + "\"externalReferenceNo\": \"20230603010101\","
                + "\"msghdr\": {"
                + "    \"msgtp\": \"DBACTLSTRQ\","
                + "    \"regapp\": \"BACKBASE\","
                + "    \"reqtmstmp\": \"20170616121017\""
                + "},"
                + "\"regdtls\": {"
                + "    \"custid\": \"3186200\","
                + "    \"inclplgbal\": \"N\","
                + "    \"inclpval\": \"N\","
                + "    \"inclpyanal\": \"N\","
                + "    \"regrefno\": \"341561758\""
                + "}"
                + "}";

        try {
            // Parse the JSON string into a JSONObject
            JSONObject json = new JSONObject(jsonString);
            String refNo = json.getString("externalReferenceNo");
            String userId = json.getString("userId");

            // Use StringBuilder to build the XML structure using constants
            StringBuilder soapEnvelope = new StringBuilder();
            soapEnvelope.append(DematOBPConstants.SOAP_ENVELOPE_START)
                        .append(DematOBPConstants.SOAP_HEADER)
                        .append(DematOBPConstants.SOAP_BODY_START)
                        .append(DematOBPConstants.DO_DEMAT_LANDING_PAGE_START)
                        .append(DematOBPConstants.ARG_START)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_BANK_CODE_START).append(json.getString("bankCode")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_BANK_CODE_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_CHANNEL_START).append(json.getString("channel")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_CHANNEL_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_TX_PARTY_CD_START).append(json.getString("transactingPartyCode")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_TX_PARTY_CD_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_TX_BRANCH_START).append(json.getString("transactionBranch")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_TX_BRANCH_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_USER_ID_START).append(userId).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_USER_ID_END)
                        .append("<con:externalReferenceNo>").append(refNo).append("</con:externalReferenceNo>")
                        .append(DematOBPConstants.ARG_END)
                        .append(DematOBPConstants.ARG1_START)
                        .append(DematOBPConstants.MSGHDR_START)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_MSG_TP_START).append(json.getJSONObject("msghdr").getString("msgtp")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_MSG_TP_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_REQAPP_START).append(json.getJSONObject("msghdr").getString("regapp")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_REQAPP_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_REQTMSTTMP_START).append(json.getJSONObject("msghdr").getString("reqtmstmp")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_REQTMSTTMP_END)
                        .append(DematOBPConstants.MSGHDR_END)
                        .append(DematOBPConstants.REGDTLS_START)
                        .append("<custid>").append(json.getJSONObject("regdtls").getString("custid")).append("</custid>")
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCL_PLGBAL_START).append(json.getJSONObject("regdtls").getString("inclplgbal")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCL_PLGBAL_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPVAL_START).append(json.getJSONObject("regdtls").getString("inclpval")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPVAL_END)
                        .append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPVANAL_START).append(json.getJSONObject("regdtls").getString("inclpyanal")).append(DematOBPConstants.OBP_DEMAT_LANDING_PAGE_INCLPVANAL_END)
                        .append("<regrefno>").append(refNo).append("</regrefno>")
                        .
