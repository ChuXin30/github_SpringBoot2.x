package com.example.chapter11.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * RestTemplate 请求日志拦截器
 * 记录HTTP请求和响应的详细信息
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {

        // 记录请求信息
        logger.info("=== HTTP Request ===");
        logger.info("Method: {}", request.getMethod());
        logger.info("URI: {}", request.getURI());
        logger.info("Headers: {}", request.getHeaders());
        
        if (body.length > 0) {
            logger.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
        }

        // 执行请求
        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        long endTime = System.currentTimeMillis();

        // 记录响应信息
        logger.info("=== HTTP Response ===");
        logger.info("Status Code: {}", response.getStatusCode());
        logger.info("Status Text: {}", response.getStatusText());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Time: {}ms", (endTime - startTime));
        logger.info("==================");

        return response;
    }
}
