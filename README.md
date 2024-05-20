package com.example.logging;

import com.example.exception.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String requestBody = extractRequestBody(wrappedRequest);
        String userAgent = request.getHeader("User-Agent");
        String errorMessage = "";
        TransactionLoggingRequest transactionLoggingRequest = null;
        boolean isTransactionLogged = false;

        try {
            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            // Extract updated request attributes if any
            String updatedParam = (String) wrappedRequest.getAttribute("exampleParam");
            if (updatedParam != null) {
                // Update the request body or log the updated parameter
                requestBody = "Updated Param: " + updatedParam;
            }

            // Extract response body
            String responseBody = extractResponseBody(wrappedResponse);
            int status = wrappedResponse.getStatus();

            // Create and populate transactionLoggingRequest here
            transactionLoggingRequest = new TransactionLoggingRequest();
            transactionLoggingRequest.setUserAgent(userAgent);
            transactionLoggingRequest.setRequestBody(requestBody);

            if (status < 300) {
                transactionLoggingUtil.logSuccessfulTransaction(requestBody, responseBody, transactionLoggingRequest);
                isTransactionLogged = true;
            } else {
                // Parse the response body to extract the error code and message
                ResponseDTO<?> responseDTO = objectMapper.readValue(responseBody, ResponseDTO.class);
                transactionLoggingUtil.logFailedTransaction(requestBody, responseDTO.getErrorCode().toString(), responseDTO.getMessage(), transactionLoggingRequest);
                isTransactionLogged = true;
            }

        } catch (Exception e) {
            errorMessage = e.getMessage();
            int status = wrappedResponse.getStatus();

            if (!isTransactionLogged) {
                // Create and populate transactionLoggingRequest here
                transactionLoggingRequest = new TransactionLoggingRequest();
                transactionLoggingRequest.setUserAgent(userAgent);
                transactionLoggingRequest.setRequestBody(requestBody);

                transactionLoggingUtil.logFailedTransaction(requestBody, String.valueOf(status), errorMessage, transactionLoggingRequest);
                isTransactionLogged = true;
            }
            throw e;
        } finally {
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String extractRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        return new String(buf, 0, buf.length);
    }

    private String extractResponseBody(ContentCachingResponseWrapper response) throws IOException {
        byte[] buf = response.getContentAsByteArray();
        response.copyBodyToResponse();
        return new String(buf, 0, buf.length);
    }
}
