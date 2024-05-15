package com.example.demo.utils;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip logging for GET requests
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Wrap the request and response to cache their content
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception e) {
            // Log exception if any occurs during request processing
            logError(e);
        } finally {
            // Log the request details
            logRequestDetails(wrappedRequest);
            // Log the response details
            logResponseDetails(wrappedResponse);
            // Ensure the response content is sent to the client
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * Logs the details of the HTTP request.
     *
     * @param request the wrapped HTTP request
     */
    private void logRequestDetails(ContentCachingRequestWrapper request) {
        String requestBody = extractRequestBody(request);
        System.out.println("Request Body: " + requestBody);
        System.out.println("Request Headers: " + extractRequestHeaders(request));
    }

    /**
     * Logs the details of the HTTP response.
     *
     * @param response the wrapped HTTP response
     */
    private void logResponseDetails(ContentCachingResponseWrapper response) {
        String responseBody = extractResponseBody(response);
        System.out.println("Response Body: " + responseBody);
    }

    /**
     * Extracts the request body from the cached request.
     *
     * @param request the wrapped HTTP request
     * @return the request body as a string
     */
    private String extractRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        return getStringValue(content, request.getCharacterEncoding());
    }

    /**
     * Extracts the response body from the cached response.
     *
     * @param response the wrapped HTTP response
     * @return the response body as a string
     */
    private String extractResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        return getStringValue(content, response.getCharacterEncoding());
    }

    /**
     * Extracts the request headers.
     *
     * @param request the wrapped HTTP request
     * @return a formatted string of request headers
     */
    private String extractRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append("\n");
        }
        return headers.toString();
    }

    /**
     * Converts byte array content to a string.
     *
     * @param content     the byte array content
     * @param charsetName the character encoding
     * @return the content as a string
     */
    private String getStringValue(byte[] content, String charsetName) {
        try {
            return new String(content, charsetName);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Logs the exception details.
     *
     * @param e the exception
     */
    private void logError(Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
    }
}
