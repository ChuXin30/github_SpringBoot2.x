package com.example.chapter11.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * API响应包装类
 * 用于包装REST API的响应数据
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    // 默认构造函数
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    // 成功响应构造函数
    public ApiResponse(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 完整构造函数
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 静态方法：创建成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    // 静态方法：创建错误响应
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // Getter和Setter方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
