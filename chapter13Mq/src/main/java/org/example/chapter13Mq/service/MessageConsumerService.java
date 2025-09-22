package org.example.chapter13Mq.service;

import org.example.chapter13Mq.model.OrderMessage;
import org.example.chapter13Mq.model.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息消费者服务
 * 负责接收和处理各种类型的消息
 * 
 * @author example
 * @since 1.0.0
 */
@Service
public class MessageConsumerService {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumerService.class);

    // 消息计数器
    private final AtomicLong userMessageCount = new AtomicLong(0);
    private final AtomicLong orderMessageCount = new AtomicLong(0);
    private final AtomicLong emailMessageCount = new AtomicLong(0);
    private final AtomicLong notificationMessageCount = new AtomicLong(0);
    private final AtomicLong newsMessageCount = new AtomicLong(0);
    private final AtomicLong weatherMessageCount = new AtomicLong(0);
    private final AtomicLong stockMessageCount = new AtomicLong(0);

    /**
     * 监听用户消息队列
     */
    @JmsListener(destination = "${activemq.queue.user}", containerFactory = "queueListenerContainerFactory")
    public void handleUserMessage(UserMessage userMessage) {
        try {
            long count = userMessageCount.incrementAndGet();
            log.info("收到用户消息 #{}: {}", count, userMessage);
            
            // 模拟处理用户消息
            processUserMessage(userMessage);
            
        } catch (Exception e) {
            log.error("处理用户消息失败: {}", userMessage, e);
        }
    }

    /**
     * 监听订单消息队列
     */
    @JmsListener(destination = "${activemq.queue.order}", containerFactory = "queueListenerContainerFactory")
    public void handleOrderMessage(OrderMessage orderMessage) {
        try {
            long count = orderMessageCount.incrementAndGet();
            log.info("收到订单消息 #{}: {}", count, orderMessage);
            
            // 模拟处理订单消息
            processOrderMessage(orderMessage);
            
        } catch (Exception e) {
            log.error("处理订单消息失败: {}", orderMessage, e);
        }
    }

    /**
     * 监听邮件消息队列
     */
    @JmsListener(destination = "${activemq.queue.email}", containerFactory = "queueListenerContainerFactory")
    public void handleEmailMessage(UserMessage emailMessage) {
        try {
            long count = emailMessageCount.incrementAndGet();
            log.info("收到邮件消息 #{}: {}", count, emailMessage);
            
            // 模拟发送邮件
            processEmailMessage(emailMessage);
            
        } catch (Exception e) {
            log.error("处理邮件消息失败: {}", emailMessage, e);
        }
    }

    /**
     * 监听通知消息队列
     */
    @JmsListener(destination = "${activemq.queue.notification}", containerFactory = "queueListenerContainerFactory")
    public void handleNotificationMessage(UserMessage notificationMessage) {
        try {
            long count = notificationMessageCount.incrementAndGet();
            log.info("收到通知消息 #{}: {}", count, notificationMessage);
            
            // 模拟发送通知
            processNotificationMessage(notificationMessage);
            
        } catch (Exception e) {
            log.error("处理通知消息失败: {}", notificationMessage, e);
        }
    }

    /**
     * 监听新闻主题
     */
    @JmsListener(destination = "${activemq.topic.news}", containerFactory = "topicListenerContainerFactory")
    public void handleNewsMessage(UserMessage newsMessage) {
        try {
            long count = newsMessageCount.incrementAndGet();
            log.info("收到新闻消息 #{}: {}", count, newsMessage);
            
            // 模拟处理新闻消息
            processNewsMessage(newsMessage);
            
        } catch (Exception e) {
            log.error("处理新闻消息失败: {}", newsMessage, e);
        }
    }

    /**
     * 监听天气主题
     */
    @JmsListener(destination = "${activemq.topic.weather}", containerFactory = "topicListenerContainerFactory")
    public void handleWeatherMessage(UserMessage weatherMessage) {
        try {
            long count = weatherMessageCount.incrementAndGet();
            log.info("收到天气消息 #{}: {}", count, weatherMessage);
            
            // 模拟处理天气消息
            processWeatherMessage(weatherMessage);
            
        } catch (Exception e) {
            log.error("处理天气消息失败: {}", weatherMessage, e);
        }
    }

    /**
     * 监听股票主题
     */
    @JmsListener(destination = "${activemq.topic.stock}", containerFactory = "topicListenerContainerFactory")
    public void handleStockMessage(UserMessage stockMessage) {
        try {
            long count = stockMessageCount.incrementAndGet();
            log.info("收到股票消息 #{}: {}", count, stockMessage);
            
            // 模拟处理股票消息
            processStockMessage(stockMessage);
            
        } catch (Exception e) {
            log.error("处理股票消息失败: {}", stockMessage, e);
        }
    }

    /**
     * 处理用户消息
     */
    private void processUserMessage(UserMessage userMessage) {
        // 模拟处理时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("用户消息处理完成: 用户={}, 类型={}", 
                userMessage.getUsername(), userMessage.getMessageType());
    }

    /**
     * 处理订单消息
     */
    private void processOrderMessage(OrderMessage orderMessage) {
        // 模拟处理时间
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("订单消息处理完成: 订单号={}, 用户={}, 金额={}", 
                orderMessage.getOrderNumber(), 
                orderMessage.getUsername(), 
                orderMessage.getTotalAmount());
    }

    /**
     * 处理邮件消息
     */
    private void processEmailMessage(UserMessage emailMessage) {
        // 模拟发送邮件时间
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("邮件发送完成: 收件人={}, 内容={}", 
                emailMessage.getEmail(), 
                emailMessage.getContent());
    }

    /**
     * 处理通知消息
     */
    private void processNotificationMessage(UserMessage notificationMessage) {
        // 模拟发送通知时间
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("通知发送完成: 用户={}, 内容={}", 
                notificationMessage.getUsername(), 
                notificationMessage.getContent());
    }

    /**
     * 处理新闻消息
     */
    private void processNewsMessage(UserMessage newsMessage) {
        // 模拟处理新闻时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("新闻消息处理完成: 内容={}", newsMessage.getContent());
    }

    /**
     * 处理天气消息
     */
    private void processWeatherMessage(UserMessage weatherMessage) {
        // 模拟处理天气时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("天气消息处理完成: 内容={}", weatherMessage.getContent());
    }

    /**
     * 处理股票消息
     */
    private void processStockMessage(UserMessage stockMessage) {
        // 模拟处理股票时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("股票消息处理完成: 内容={}", stockMessage.getContent());
    }

    /**
     * 获取消息统计信息
     */
    public String getMessageStatistics() {
        return String.format(
            "消息统计 - 用户消息: %d, 订单消息: %d, 邮件消息: %d, 通知消息: %d, " +
            "新闻消息: %d, 天气消息: %d, 股票消息: %d",
            userMessageCount.get(),
            orderMessageCount.get(),
            emailMessageCount.get(),
            notificationMessageCount.get(),
            newsMessageCount.get(),
            weatherMessageCount.get(),
            stockMessageCount.get()
        );
    }

    /**
     * 重置消息计数器
     */
    public void resetMessageCounters() {
        userMessageCount.set(0);
        orderMessageCount.set(0);
        emailMessageCount.set(0);
        notificationMessageCount.set(0);
        newsMessageCount.set(0);
        weatherMessageCount.set(0);
        stockMessageCount.set(0);
        log.info("消息计数器已重置");
    }
}
