import org.springframework.web.util.ContentCachingRequestWrapper;

private String extractRequestBody(HttpServletRequest request) {
    try {
        // Wrap the request in ContentCachingRequestWrapper
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        // Read request body from wrapped request
        byte[] requestBodyBytes = wrappedRequest.getContentAsByteArray();
        if (requestBodyBytes != null && requestBodyBytes.length > 0) {
            return new String(requestBodyBytes, wrappedRequest.getCharacterEncoding());
        } else {
            return null; // No request body present
        }
    } catch (Exception e) {
        e.printStackTrace();
        return null; // Error occurred while extracting request body
    }
}
