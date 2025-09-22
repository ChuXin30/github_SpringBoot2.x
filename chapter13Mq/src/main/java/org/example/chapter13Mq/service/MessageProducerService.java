package org.example.chapter13Mq.service;

import org.example.chapter13Mq.model.OrderMessage;
import org.example.chapter13Mq.model.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

/**
 * 消息生产者服务
 * 负责发送各种类型的消息到ActiveMQ
 * 
 * @author example
 * @since 1.0.0
 */
@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${activemq.queue.user}")
    private String userQueue;

    @Value("${activemq.queue.order}")
    private String orderQueue;

    @Value("${activemq.queue.email}")
    private String emailQueue;

    @Value("${activemq.queue.notification}")
    private String notificationQueue;

    @Value("${activemq.topic.news}")
    private String newsTopic;

    @Value("${activemq.topic.weather}")
    private String weatherTopic;

    @Value("${activemq.topic.stock}")
    private String stockTopic;

    /**
     * 发送用户消息到队列
     */
    public void sendUserMessage(UserMessage userMessage) {
        try {
            userMessage.setMessageType("USER_MESSAGE");
            jmsTemplate.convertAndSend(userQueue, userMessage);
            log.info("用户消息已发送到队列 {}: {}", userQueue, userMessage);
        } catch (Exception e) {
            log.error("发送用户消息失败", e);
            throw new RuntimeException("发送用户消息失败", e);
        }
    }

    /**
     * 发送订单消息到队列
     */
    public void sendOrderMessage(OrderMessage orderMessage) {
        try {
            orderMessage.setMessageType("ORDER_MESSAGE");
            jmsTemplate.convertAndSend(orderQueue, orderMessage);
            log.info("订单消息已发送到队列 {}: {}", orderQueue, orderMessage);
        } catch (Exception e) {
            log.error("发送订单消息失败", e);
            throw new RuntimeException("发送订单消息失败", e);
        }
    }

    /**
     * 发送邮件消息到队列
     */
    public void sendEmailMessage(String to, String subject, String content) {
        try {
            UserMessage emailMessage = new UserMessage();
            emailMessage.setUsername(to);
            emailMessage.setEmail(to);
            emailMessage.setContent("主题: " + subject + "\n内容: " + content);
            emailMessage.setMessageType("EMAIL_MESSAGE");
            
            jmsTemplate.convertAndSend(emailQueue, emailMessage);
            log.info("邮件消息已发送到队列 {}: 收件人={}, 主题={}", emailQueue, to, subject);
        } catch (Exception e) {
            log.error("发送邮件消息失败", e);
            throw new RuntimeException("发送邮件消息失败", e);
        }
    }

    /**
     * 发送通知消息到队列
     */
    public void sendNotificationMessage(String username, String title, String content) {
        try {
            UserMessage notificationMessage = new UserMessage();
            notificationMessage.setUsername(username);
            notificationMessage.setContent("标题: " + title + "\n内容: " + content);
            notificationMessage.setMessageType("NOTIFICATION_MESSAGE");
            
            jmsTemplate.convertAndSend(notificationQueue, notificationMessage);
            log.info("通知消息已发送到队列 {}: 用户={}, 标题={}", notificationQueue, username, title);
        } catch (Exception e) {
            log.error("发送通知消息失败", e);
            throw new RuntimeException("发送通知消息失败", e);
        }
    }

    /**
     * 发布新闻消息到主题
     */
    public void publishNewsMessage(String title, String content, String category) {
        try {
            UserMessage newsMessage = new UserMessage();
            newsMessage.setUsername("SYSTEM");
            newsMessage.setContent("标题: " + title + "\n内容: " + content + "\n分类: " + category);
            newsMessage.setMessageType("NEWS_MESSAGE");
            
            jmsTemplate.convertAndSend(newsTopic, newsMessage);
            log.info("新闻消息已发布到主题 {}: 标题={}, 分类={}", newsTopic, title, category);
        } catch (Exception e) {
            log.error("发布新闻消息失败", e);
            throw new RuntimeException("发布新闻消息失败", e);
        }
    }

    /**
     * 发布天气消息到主题
     */
    public void publishWeatherMessage(String city, String weather, String temperature) {
        try {
            UserMessage weatherMessage = new UserMessage();
            weatherMessage.setUsername("WEATHER_SYSTEM");
            weatherMessage.setContent("城市: " + city + "\n天气: " + weather + "\n温度: " + temperature);
            weatherMessage.setMessageType("WEATHER_MESSAGE");
            
            jmsTemplate.convertAndSend(weatherTopic, weatherMessage);
            log.info("天气消息已发布到主题 {}: 城市={}, 天气={}", weatherTopic, city, weather);
        } catch (Exception e) {
            log.error("发布天气消息失败", e);
            throw new RuntimeException("发布天气消息失败", e);
        }
    }

    /**
     * 发布股票消息到主题
     */
    public void publishStockMessage(String symbol, String price, String change) {
        try {
            UserMessage stockMessage = new UserMessage();
            stockMessage.setUsername("STOCK_SYSTEM");
            stockMessage.setContent("股票代码: " + symbol + "\n价格: " + price + "\n涨跌: " + change);
            stockMessage.setMessageType("STOCK_MESSAGE");
            
            jmsTemplate.convertAndSend(stockTopic, stockMessage);
            log.info("股票消息已发布到主题 {}: 代码={}, 价格={}", stockTopic, symbol, price);
        } catch (Exception e) {
            log.error("发布股票消息失败", e);
            throw new RuntimeException("发布股票消息失败", e);
        }
    }

    /**
     * 发送简单文本消息
     */
    public void sendTextMessage(String destination, String message) {
        try {
            jmsTemplate.convertAndSend(destination, message);
            log.info("文本消息已发送到目标 {}: {}", destination, message);
        } catch (Exception e) {
            log.error("发送文本消息失败", e);
            throw new RuntimeException("发送文本消息失败", e);
        }
    }

    /**
     * 批量发送用户消息
     */
    public void sendBatchUserMessages(int count) {
        try {
            for (int i = 1; i <= count; i++) {
                UserMessage userMessage = new UserMessage();
                userMessage.setUsername("user" + i);
                userMessage.setEmail("user" + i + "@example.com");
                userMessage.setContent("这是第 " + i + " 条批量消息");
                userMessage.setMessageType("BATCH_USER_MESSAGE");
                
                jmsTemplate.convertAndSend(userQueue, userMessage);
            }
            log.info("批量发送了 {} 条用户消息到队列 {}", count, userQueue);
        } catch (Exception e) {
            log.error("批量发送用户消息失败", e);
            throw new RuntimeException("批量发送用户消息失败", e);
        }
    }

    /**
     * 创建示例订单消息
     */
    public OrderMessage createSampleOrderMessage() {
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        orderMessage.setUserId(1001L);
        orderMessage.setUsername("张三");
        orderMessage.setStatus("PENDING");
        orderMessage.setTotalAmount(new BigDecimal("299.99"));
        
        // 创建订单项
        OrderMessage.OrderItem item1 = new OrderMessage.OrderItem(1L, "iPhone 15", 1, new BigDecimal("299.99"));
        orderMessage.setItems(Arrays.asList(item1));
        
        return orderMessage;
    }

    /**
     * 创建示例用户消息
     */
    public UserMessage createSampleUserMessage() {
        UserMessage userMessage = new UserMessage();
        userMessage.setUsername("李四");
        userMessage.setEmail("lisi@example.com");
        userMessage.setContent("欢迎注册我们的服务！");
        userMessage.setMessageType("WELCOME_MESSAGE");
        
        return userMessage;
    }
}
