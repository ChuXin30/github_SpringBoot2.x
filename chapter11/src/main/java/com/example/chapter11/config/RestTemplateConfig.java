package com.example.chapter11.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 * 配置RestTemplate的各种参数和拦截器
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建RestTemplate Bean
     * 配置超时时间、连接池等参数
     */
    @Bean
    public RestTemplate restTemplate() {
        // 创建HttpClient
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)           // 最大连接数
                .setMaxConnPerRoute(20)         // 每个路由的最大连接数
                .build();

        // 创建HttpComponentsClientHttpRequestFactory
        HttpComponentsClientHttpRequestFactory factory = 
                new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(5000);        // 连接超时时间：5秒
        factory.setReadTimeout(10000);          // 读取超时时间：10秒

        // 创建RestTemplate
        RestTemplate restTemplate = new RestTemplate(factory);

        // 添加请求拦截器
        restTemplate.getInterceptors().add(new LoggingRequestInterceptor());

        return restTemplate;
    }

    /**
     * 创建简单的RestTemplate Bean（无特殊配置）
     */
    @Bean("simpleRestTemplate")
    public RestTemplate simpleRestTemplate() {
        return new RestTemplate();
    }
}
