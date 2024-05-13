import org.springframework.web.util.ContentCachingRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

private String extractRequestBody(HttpServletRequest request) {
    try {
        // Wrap the request in ContentCachingRequestWrapper
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        // Read request body from wrapped request
        byte[] requestBodyBytes = wrappedRequest.getContentAsByteArray();
        if (requestBodyBytes != null && requestBodyBytes.length > 0) {
            String requestBody = new String(requestBodyBytes, wrappedRequest.getCharacterEncoding());
            logger.debug("Request Body: {}", requestBody);
            return requestBody;
        } else {
            logger.debug("Request Body is empty");
            return null; // No request body present
        }
    } catch (Exception e) {
        logger.error("Error extracting request body", e);
        return null; // Error occurred while extracting request body
    }
}
