package com.lox.configserver.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Cast to HTTP-specific classes
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Capture request details
        String clientIP = request.getRemoteAddr();
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String fullURL = uri + (queryString != null ? "?" + queryString : "");
        String headers = getHeadersInfo(httpRequest);

        // Log incoming request
        log.info("\n" +
                "*******************************************\n" +
                "* Incoming Request                         *\n" +
                "*******************************************\n" +
                "IP          : {}\n" +
                "Method      : {}\n" +
                "URL         : {}\n" +
                "Headers     : {}\n" +
                "*******************************************", clientIP, method, fullURL, headers);

        // Record the start time for calculating processing time
        long startTime = System.currentTimeMillis();

        try {
            // Proceed with the next filter in the chain
            chain.doFilter(request, response);
        } catch (Exception e) {
            // Log any exception that occurs
            log.error("\n" +
                    "*******************************************\n" +
                    "* Exception Occurred                       *\n" +
                    "*******************************************\n" +
                    "Message     : {}\n" +
                    "StackTrace  : {}\n" +
                    "*******************************************", e.getMessage(), e);
            throw e; // Re-throw the exception
        } finally {
            // Calculate processing time
            long duration = System.currentTimeMillis() - startTime;

            // Log outgoing response
            int status = httpResponse.getStatus();
            log.info("\n" +
                    "*******************************************\n" +
                    "* Outgoing Response                        *\n" +
                    "*******************************************\n" +
                    "IP          : {}\n" +
                    "Method      : {}\n" +
                    "URL         : {}\n" +
                    "Status      : {}\n" +
                    "Processing Time : {}ms\n" +
                    "*******************************************", clientIP, method, fullURL, status, duration);
        }
    }

    /**
     * Helper method to extract headers from the HttpServletRequest.
     *
     * @param request the HttpServletRequest
     * @return a formatted string of headers
     */
    private String getHeadersInfo(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return "";
        }

        StringBuilder headersBuilder = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // Mask sensitive headers like Authorization
            if ("Authorization".equalsIgnoreCase(headerName)) {
                headersBuilder.append(headerName).append("=***** ");
            } else {
                headersBuilder.append(headerName).append("=").append(headerValue).append(" ");
            }
        }
        return headersBuilder.toString().trim();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization logic if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
