import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentParser {

    public static void main(String[] args) {
        String userAgent = "Your User Agent String Here";
        parseAndPrintUserAgent(userAgent);
    }

    public static void parseAndPrintUserAgent(String userAgent) {
        try {
            Parser parser = new Parser();
            Client client = parser.parse(userAgent);

            String osFamily = (client.os.family != null) ? client.os.family : "NA";
            String osVersion = "NA";
            if (client.os.major != null) {
                osVersion = client.os.major;
                if (client.os.minor != null) {
                    osVersion += "." + client.os.minor;
                    if (client.os.patch != null) {
                        osVersion += "." + client.os.patch;
                    }
                }
            }

            String browserName = (client.userAgent.family != null) ? client.userAgent.family : "NA";
            String browserVersion = "NA";
            if (client.userAgent.major != null) {
                browserVersion = client.userAgent.major;
                if (client.userAgent.minor != null) {
                    browserVersion += "." + client.userAgent.minor;
                    if (client.userAgent.patch != null) {
                        browserVersion += "." + client.userAgent.patch;
                    }
                }
            }

            System.out.println("device_os_platform");
            System.out.println(osFamily + " " + osVersion);
            System.out.println("device_os_platform");
            System.out.println(osFamily);
            System.out.println("Browser_version");
            System.out.println(browserName + " " + browserVersion);
        } catch (Exception e) {
            System.out.println("device_os_platform");
            System.out.println("NA NA");
            System.out.println("device_os_platform");
            System.out.println("NA");
            System.out.println("Browser_version");
            System.out.println("NA NA");
        }
    }
}
