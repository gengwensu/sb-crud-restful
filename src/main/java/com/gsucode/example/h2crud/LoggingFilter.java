package com.gsucode.example.h2crud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String uri = request.getRequestURI();
            if (uri != null
                    && !uri.contains("swagger-ui")
                    && !uri.contains("api-docs")
                    && !uri.contains("swagger-resources")) {
                log.info("\nHTTP {} ~{}\nContent-Type: {}\nHeaders: {}\nBody: {}\nResponse-Code: {}\nResponse-body: {}\n",
                        requestWrapper.getMethod(),
                        uri,
                        requestWrapper.getContentType(),
                        new ServletServerHttpRequest(requestWrapper).getHeaders(),
                        new String(requestWrapper.getContentAsByteArray()),
                        responseWrapper.getStatusCode(),
                        new String(responseWrapper.getContentAsByteArray()));
            }
            responseWrapper.copyBodyToResponse();
        }
    }
}
