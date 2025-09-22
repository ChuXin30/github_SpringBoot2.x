package org.example.chapter13Mq.controller;

import org.example.chapter13Mq.model.OrderMessage;
import org.example.chapter13Mq.model.UserMessage;
import org.example.chapter13Mq.service.MessageConsumerService;
import org.example.chapter13Mq.service.MessageProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ActiveMQ 演示控制器
 * 提供各种消息发送和接收的API接口
 * 
 * @author example
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/activemq")
public class ActiveMQController {

    private static final Logger log = LoggerFactory.getLogger(ActiveMQController.class);

    @Autowired
    private MessageProducerService messageProducerService;

    @Autowired
    private MessageConsumerService messageConsumerService;

    /**
     * 发送用户消息
     */
    @PostMapping("/send/user")
    public Map<String, Object> sendUserMessage(@RequestBody UserMessage userMessage) {
        log.info("接收到发送用户消息请求: {}", userMessage);
        
        try {
            messageProducerService.sendUserMessage(userMessage);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "用户消息发送成功");
            result.put("data", userMessage);
            
            return result;
        } catch (Exception e) {
            log.error("发送用户消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发送订单消息
     */
    @PostMapping("/send/order")
    public Map<String, Object> sendOrderMessage(@RequestBody OrderMessage orderMessage) {
        log.info("接收到发送订单消息请求: {}", orderMessage);
        
        try {
            messageProducerService.sendOrderMessage(orderMessage);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "订单消息发送成功");
            result.put("data", orderMessage);
            
            return result;
        } catch (Exception e) {
            log.error("发送订单消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发送邮件消息
     */
    @PostMapping("/send/email")
    public Map<String, Object> sendEmailMessage(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {
        log.info("接收到发送邮件消息请求: to={}, subject={}", to, subject);
        
        try {
            messageProducerService.sendEmailMessage(to, subject, content);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "邮件消息发送成功");
            result.put("data", Map.of("to", to, "subject", subject, "content", content));
            
            return result;
        } catch (Exception e) {
            log.error("发送邮件消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "邮件消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发送通知消息
     */
    @PostMapping("/send/notification")
    public Map<String, Object> sendNotificationMessage(
            @RequestParam String username,
            @RequestParam String title,
            @RequestParam String content) {
        log.info("接收到发送通知消息请求: username={}, title={}", username, title);
        
        try {
            messageProducerService.sendNotificationMessage(username, title, content);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "通知消息发送成功");
            result.put("data", Map.of("username", username, "title", title, "content", content));
            
            return result;
        } catch (Exception e) {
            log.error("发送通知消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "通知消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发布新闻消息
     */
    @PostMapping("/publish/news")
    public Map<String, Object> publishNewsMessage(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String category) {
        log.info("接收到发布新闻消息请求: title={}, category={}", title, category);
        
        try {
            messageProducerService.publishNewsMessage(title, content, category);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "新闻消息发布成功");
            result.put("data", Map.of("title", title, "content", content, "category", category));
            
            return result;
        } catch (Exception e) {
            log.error("发布新闻消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "新闻消息发布失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发布天气消息
     */
    @PostMapping("/publish/weather")
    public Map<String, Object> publishWeatherMessage(
            @RequestParam String city,
            @RequestParam String weather,
            @RequestParam String temperature) {
        log.info("接收到发布天气消息请求: city={}, weather={}", city, weather);
        
        try {
            messageProducerService.publishWeatherMessage(city, weather, temperature);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "天气消息发布成功");
            result.put("data", Map.of("city", city, "weather", weather, "temperature", temperature));
            
            return result;
        } catch (Exception e) {
            log.error("发布天气消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "天气消息发布失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发布股票消息
     */
    @PostMapping("/publish/stock")
    public Map<String, Object> publishStockMessage(
            @RequestParam String symbol,
            @RequestParam String price,
            @RequestParam String change) {
        log.info("接收到发布股票消息请求: symbol={}, price={}", symbol, price);
        
        try {
            messageProducerService.publishStockMessage(symbol, price, change);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "股票消息发布成功");
            result.put("data", Map.of("symbol", symbol, "price", price, "change", change));
            
            return result;
        } catch (Exception e) {
            log.error("发布股票消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "股票消息发布失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发送文本消息
     */
    @PostMapping("/send/text")
    public Map<String, Object> sendTextMessage(
            @RequestParam String destination,
            @RequestParam String message) {
        log.info("接收到发送文本消息请求: destination={}, message={}", destination, message);
        
        try {
            messageProducerService.sendTextMessage(destination, message);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "文本消息发送成功");
            result.put("data", Map.of("destination", destination, "message", message));
            
            return result;
        } catch (Exception e) {
            log.error("发送文本消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "文本消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 批量发送用户消息
     */
    @PostMapping("/send/batch")
    public Map<String, Object> sendBatchUserMessages(@RequestParam(defaultValue = "10") int count) {
        log.info("接收到批量发送用户消息请求: count={}", count);
        
        try {
            messageProducerService.sendBatchUserMessages(count);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量用户消息发送成功");
            result.put("data", Map.of("count", count));
            
            return result;
        } catch (Exception e) {
            log.error("批量发送用户消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量用户消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 发送示例消息
     */
    @PostMapping("/send/sample")
    public Map<String, Object> sendSampleMessages() {
        log.info("接收到发送示例消息请求");
        
        try {
            // 发送示例用户消息
            UserMessage userMessage = messageProducerService.createSampleUserMessage();
            messageProducerService.sendUserMessage(userMessage);
            
            // 发送示例订单消息
            OrderMessage orderMessage = messageProducerService.createSampleOrderMessage();
            messageProducerService.sendOrderMessage(orderMessage);
            
            // 发送示例邮件消息
            messageProducerService.sendEmailMessage("test@example.com", "测试邮件", "这是一封测试邮件");
            
            // 发送示例通知消息
            messageProducerService.sendNotificationMessage("张三", "系统通知", "您的订单已处理完成");
            
            // 发布示例新闻消息
            messageProducerService.publishNewsMessage("科技新闻", "最新科技动态", "科技");
            
            // 发布示例天气消息
            messageProducerService.publishWeatherMessage("北京", "晴天", "25°C");
            
            // 发布示例股票消息
            messageProducerService.publishStockMessage("AAPL", "$150.00", "+2.5%");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "示例消息发送成功");
            result.put("data", Map.of(
                "userMessage", userMessage,
                "orderMessage", orderMessage
            ));
            
            return result;
        } catch (Exception e) {
            log.error("发送示例消息失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "示例消息发送失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 获取消息统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getMessageStatistics() {
        log.info("接收到获取消息统计信息请求");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "消息统计信息获取成功");
        result.put("data", messageConsumerService.getMessageStatistics());
        
        return result;
    }

    /**
     * 重置消息计数器
     */
    @PostMapping("/reset")
    public Map<String, Object> resetMessageCounters() {
        log.info("接收到重置消息计数器请求");
        
        try {
            messageConsumerService.resetMessageCounters();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "消息计数器重置成功");
            
            return result;
        } catch (Exception e) {
            log.error("重置消息计数器失败", e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "消息计数器重置失败: " + e.getMessage());
            
            return result;
        }
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/system-info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
        info.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        info.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "系统信息获取成功");
        result.put("data", info);
        
        return result;
    }
}
