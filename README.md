import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ForwardingFilter extends OncePerRequestFilter {

    private final WebClientUtil webClientUtil;
    private final YamlConfig yamlConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    public ForwardingFilter(WebClientUtil webClientUtil, YamlConfig yamlConfig) {
        this.webClientUtil = webClientUtil;
        this.yamlConfig = yamlConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String fullRequestURI = request.getRequestURI();
        String contextPath = request.getServletContext().getContextPath();
        String requestURI = fullRequestURI.substring(contextPath.length());

        if (requestURI.startsWith("/ibmb/")) {
            try {
                byte[] requestBody = StreamUtils.copyToByteArray(request.getInputStream());
                String requestBodyStr = new String(requestBody, request.getCharacterEncoding());

                // Headers for forwarding request
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                // Try forwarding to DEV first
                ResponseEntity<String> devResponse = webClientUtil.makeRequest(
                        yamlConfig.getIssuerServiceurlDev() + requestURI,
                        HttpMethod.POST,
                        requestBodyStr,
                        String.class,
                        headers
                );

                if (isSuccessful(devResponse)) {
                    log.info("DEV Request successful, forwarding response to client.");
                    sendResponse(response, devResponse);
                    return;
                }

                // If DEV fails, try forwarding to SIT
                ResponseEntity<String> sitResponse = webClientUtil.makeRequest(
                        yamlConfig.getIssuerServiceurlSit() + requestURI,
                        HttpMethod.POST,
                        requestBodyStr,
                        String.class,
                        headers
                );

                if (isSuccessful(sitResponse)) {
                    log.info("SIT Request successful, forwarding response to client.");
                    sendResponse(response, sitResponse);
                    return;
                }

                // If both fail, let the request proceed to the actual controller
                log.warn("Both DEV & SIT failed, continuing request processing.");
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                log.error("Error handling IBMB request", e);
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
            }
            return;
        }

        // Continue normal processing for non-IBMB requests
        filterChain.doFilter(request, response);
    }

    /**
     * Validates if the response is considered successful (Status Code 2xx + "result": "SUCCESS").
     */
    private boolean isSuccessful(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                Map<String, Object> responseMap = mapper.readValue(response.getBody(), new TypeReference<>() {});
                String result = (String) responseMap.get("result");
                return "SUCCESS".equalsIgnoreCase(result);
            } catch (Exception e) {
                log.error("Failed to parse response body for validation", e);
            }
        }
        return false;
    }

    /**
     * Sends the received response to the original client.
     */
    private void sendResponse(HttpServletResponse response, ResponseEntity<String> serviceResponse) throws IOException {
        response.setStatus(serviceResponse.getStatusCodeValue());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(serviceResponse.getBody());
    }

    /**
     * Sends an error response with a given status and message.
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> errorResponse = Map.of("status", status, "error", message);
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
