import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private StringWriter sw = new StringWriter();

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(sw);
    }

    public String getResponseBody() {
        return sw.toString();
    }
}


private String extractResponseBody(HttpServletResponse response) {
    ResponseWrapper responseWrapper = new ResponseWrapper(response);
    // Get the response body before it's sent to the client
    String responseBody = responseWrapper.getResponseBody();
    return responseBody;
}
