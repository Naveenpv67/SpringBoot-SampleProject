import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

    private String requestBody;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        // Capture the request body when the wrapper is created
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
            requestBody = null;
        }
    }

    public String getRequestBody() {
        return requestBody;
    }
}


private String extractRequestBody(HttpServletRequest request) {
    RequestWrapper requestWrapper = new RequestWrapper(request);
    // Get the request body using the wrapper
    return requestWrapper.getRequestBody();
}

