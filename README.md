import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

    private String requestBody;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        // Capture the request body when the wrapper is created
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            requestBody = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            requestBody = null;
        }
    }

    public String getRequestBody() {
        return requestBody;
    }
}
