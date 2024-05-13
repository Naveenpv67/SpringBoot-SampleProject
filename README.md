import org.springframework.web.util.ContentCachingRequestWrapper;

private String extractRequestBody(HttpServletRequest request) {
    // Wrap the request in ContentCachingRequestWrapper
    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    // Read request body from wrapped request
    String requestBody = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
    // Process the request body as needed
    return requestBody;
}
