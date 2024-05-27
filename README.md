import ua_parser.Client;
import ua_parser.Parser;
import ua_parser.ParseException;

public class UserAgentParser {

    private static final String UNKNOWN_OS = "Unknown OS";
    private static final String UNKNOWN_BROWSER = "Unknown Browser";
    private static final String ZERO = "0";
    private static final String DOT = ".";
    private static final String SPACE = " ";
    private static final String DEVICE_OS_PLATFORM = "device_os_platform";
    private static final String BROWSER_VERSION = "Browser_version";
    private static final String USER_AGENT_STRING = "Your User Agent String Here";

    public static void main(String[] args) {
        parseAndPrintUserAgent(USER_AGENT_STRING);
    }

    public static void parseAndPrintUserAgent(String userAgent) {
        try {
            Parser parser = new Parser();
            Client client = parser.parse(userAgent);

            String os = client.os.family != null ? client.os.family : UNKNOWN_OS;
            String osVersionMajor = client.os.major != null ? client.os.major : ZERO;
            String osVersionMinor = client.os.minor != null ? client.os.minor : ZERO;
            String osVersionPatch = client.os.patch != null ? client.os.patch : "";

            StringBuilder osVersionBuilder = new StringBuilder();
            osVersionBuilder.append(osVersionMajor).append(DOT).append(osVersionMinor);
            if (!osVersionPatch.isEmpty()) {
                osVersionBuilder.append(DOT).append(osVersionPatch);
            }
            String osVersion = osVersionBuilder.toString();

            String browserName = client.userAgent.family != null ? client.userAgent.family : UNKNOWN_BROWSER;
            String browserVMajor = client.userAgent.major != null ? client.userAgent.major : ZERO;
            String browserVMinor = client.userAgent.minor != null ? client.userAgent.minor : ZERO;
            String browserVPatch = client.userAgent.patch != null ? client.userAgent.patch : "";

            StringBuilder browserVersionBuilder = new StringBuilder();
            browserVersionBuilder.append(browserVMajor).append(DOT).append(browserVMinor);
            if (!browserVPatch.isEmpty()) {
                browserVersionBuilder.append(DOT).append(browserVPatch);
            }
            String browserVersion = browserVersionBuilder.toString();

            System.out.println(DEVICE_OS_PLATFORM);
            System.out.println(os + SPACE + osVersion);
            System.out.println(DEVICE_OS_PLATFORM);
            System.out.println(os);
            System.out.println(BROWSER_VERSION);
            System.out.println(browserName + SPACE + browserVersion);
        } catch (ParseException e) {
            System.err.println("Failed to parse the user agent string.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}
