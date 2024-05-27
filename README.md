import ua_parser.Client;
import ua_parser.Parser;
import ua_parser.ParseException;

import java.util.Optional;

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

            String os = Optional.ofNullable(client.os.family).orElse(UNKNOWN_OS);

            StringBuilder osVersionBuilder = new StringBuilder();
            osVersionBuilder.append(Optional.ofNullable(client.os.major).orElse(ZERO))
                            .append(DOT)
                            .append(Optional.ofNullable(client.os.minor).orElse(ZERO))
                            .append(DOT)
                            .append(Optional.ofNullable(client.os.patch).orElse(ZERO));
            String osVersion = osVersionBuilder.toString();

            StringBuilder browserNameBuilder = new StringBuilder(Optional.ofNullable(client.userAgent.family).orElse(UNKNOWN_BROWSER));

            if (!browserNameBuilder.toString().equals(UNKNOWN_BROWSER)) {
                browserNameBuilder.append(SPACE)
                                  .append(Optional.ofNullable(client.userAgent.major).orElse(ZERO))
                                  .append(DOT)
                                  .append(Optional.ofNullable(client.userAgent.minor).orElse(ZERO))
                                  .append(DOT)
                                  .append(Optional.ofNullable(client.userAgent.patch).orElse(ZERO));
            }
            String browserName = browserNameBuilder.toString();

            StringBuilder output = new StringBuilder();
            output.append(DEVICE_OS_PLATFORM).append("\n")
                  .append(os).append(SPACE).append(osVersion).append("\n")
                  .append(DEVICE_OS_PLATFORM).append("\n")
                  .append(os).append("\n")
                  .append(BROWSER_VERSION).append("\n")
                  .append(browserName);

            System.out.println(output.toString());
        } catch (ParseException e) {
            System.err.println("Failed to parse the user agent string.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}
