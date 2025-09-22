package org.example.chapter14.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户消息模型
 * 
 * @author example
 * @since 1.0.0
 */
public class UserMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String content;
    private LocalDateTime createTime;
    private String messageType;

    public UserMessage() {
        this.createTime = LocalDateTime.now();
    }

    public UserMessage(String username, String email, String content, String messageType) {
        this();
        this.username = username;
        this.email = email;
        this.content = content;
        this.messageType = messageType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
