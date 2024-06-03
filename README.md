public class SoapXmlConstants {
    public static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String SOAP_ENVELOPE_START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " xmlns:ing=\"http://inquiry.service.demat.appx.cz.fc.ofss.com/\""
            + " xmlns:con=\"http://context.app.fc.ofss.com\""
            + " xmlns:exc=\"http://exception.infra.fc.ofss.com\""
            + " xmlns:dat=\"http://datatype.fc.ofss.com\""
            + " xmlns:dto=\"http://dto.common.domain.framework.fc.ofss.com\""
            + " xmlns:dom=\"http://domain.framework.fc.ofss.com\">";
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

            // Use StringBuilder to build the XML structure using constants
            StringBuilder soapEnvelope = new StringBuilder();
            soapEnvelope.append(SoapXmlConstants.XML_VERSION)
                        .append(SoapXmlConstants.SOAP_ENVELOPE_START)
                        .append(SoapXmlConstants.SOAP_HEADER)
                        .append(SoapXmlConstants.SOAP_BODY_START)
                        .append(SoapXmlConstants.DO_DEMAT_LANDING_PAGE_START)
                        .append(SoapXmlConstants.ARG_START)
                        .append("               <con:bankCode>").append(json.getString("bankCode")).append("</con:bankCode>")
                        .append("               <con:channel>").append(json.getString("channel")).append("</con:channel>")
                        .append("               <con:transactingPartyCode>").append(json.getString("transactingPartyCode")).append("</con:transactingPartyCode>")
                        .append("               <con:transactionBranch>").append(json.getString("transactionBranch")).append("</con:transactionBranch>")
                        .append("               <con:userId>").append(json.getString("userId")).append("</con:userId>")
                        .append("               <con:externalReferenceNo>").append(json.getString("externalReferenceNo")).append("</con:externalReferenceNo>")
                        .append(SoapXmlConstants.ARG_END)
                        .append(SoapXmlConstants.ARG1_START)
                        .append(SoapXmlConstants.MSGHDR_START)
                        .append("                   <msgtp>").append(json.getJSONObject("msghdr").getString("msgtp")).append("</msgtp>")
                        .append("                   <regapp>").append(json.getJSONObject("msghdr").getString("regapp")).append("</regapp>")
                        .append("                   <reqtmstmp>").append(json.getJSONObject("msghdr").getString("reqtmstmp")).append("</reqtmstmp>")
                        .append(SoapXmlConstants.MSGHDR_END)
                        .append(SoapXmlConstants.REGDTLS_START)
                        .append("                   <custid>").append(json.getJSONObject("regdtls").getString("custid")).append("</custid>")
                        .append("                   <inclplgbal>").append(json.getJSONObject("regdtls").getString("inclplgbal")).append("</inclplgbal>")
                        .append("                   <inclpval>").append(json.getJSONObject("regdtls").getString("inclpval")).append("</inclpval>")
                        .append("                   <inclpyanal>").append(json.getJSONObject("regdtls").getString("inclpyanal")).append("</inclpyanal>")
                        .append("                   <regrefno>").append(json.getJSONObject("regdtls").getString("regrefno")).append("</regrefno>")
                        .append(SoapXmlConstants.REGDTLS_END)
                        .append(SoapXmlConstants.ARG1_END)
                        .append(SoapXmlConstants.DO_DEMAT_LANDING_PAGE_END)
                        .append(SoapXmlConstants.SOAP_BODY_END)
                        .append(SoapXmlConstants.SOAP_ENVELOPE_END);

            // Output the final SOAP XML string
            System.out.println(soapEnvelope.toString());
        } catch (JSONException je) {
            System.out.println(je.toString());
        }
    }
}
