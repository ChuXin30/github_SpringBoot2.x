package org.example.chapter13Mq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

/**
 * ActiveMQ 配置类
 * 配置连接工厂、消息转换器、JMS模板等
 * 
 * @author example
 * @since 1.0.0
 */
@Configuration
@EnableJms
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    /**
     * 连接工厂配置
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        
        // 设置信任的包
        connectionFactory.setTrustedPackages(java.util.Arrays.asList(
            "org.example.chapter14.model",
            "java.util",
            "java.lang"
        ));
        
        return connectionFactory;
    }

    /**
     * 连接池工厂配置
     */
    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory());
        pooledConnectionFactory.setMaxConnections(10);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(500);
        pooledConnectionFactory.setBlockIfSessionPoolIsFull(true);
        pooledConnectionFactory.setIdleTimeout(30000);
        pooledConnectionFactory.setExpiryTimeout(0);
        pooledConnectionFactory.setCreateConnectionOnStartup(true);
        pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(15000);
        pooledConnectionFactory.setReconnectOnException(true);
        
        return pooledConnectionFactory;
    }

    /**
     * 消息转换器配置
     * 用于将Java对象转换为JMS消息
     */
    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        
        // 注册 JSR310 模块以支持 Java 8 时间类型
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(objectMapper);
        
        return converter;
    }

    /**
     * JMS模板配置
     * 用于发送消息
     */
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(pooledConnectionFactory());
        template.setMessageConverter(messageConverter());
        template.setDefaultDestinationName("default.queue");
        template.setDeliveryPersistent(true);
        template.setTimeToLive(60000);
        template.setPriority(4);
        return template;
    }

    /**
     * 队列监听器容器工厂配置
     */
    @Bean
    public JmsListenerContainerFactory<?> queueListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(pooledConnectionFactory());
        factory.setMessageConverter(messageConverter());
        factory.setConcurrency("1-5");
        factory.setPubSubDomain(false); // 设置为队列模式
        return factory;
    }

    /**
     * 主题监听器容器工厂配置
     */
    @Bean
    public JmsListenerContainerFactory<?> topicListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(pooledConnectionFactory());
        factory.setMessageConverter(messageConverter());
        factory.setConcurrency("1-3");
        factory.setPubSubDomain(true); // 设置为主题模式
        return factory;
    }
}
