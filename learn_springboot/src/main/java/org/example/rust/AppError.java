package org.example.rust;

import java.util.Map;
import java.util.HashMap;

/**
 * Rust风格的错误类型枚举
 * 类似于Rust的Error trait和具体错误类型
 */
public enum AppError {
    // 验证错误
    VALIDATION_ERROR("VALIDATION_ERROR", "数据验证失败"),
    
    // 数据库错误
    DATABASE_ERROR("DATABASE_ERROR", "数据库操作失败"),
    RECORD_NOT_FOUND("RECORD_NOT_FOUND", "记录未找到"),
    DUPLICATE_RECORD("DUPLICATE_RECORD", "记录已存在"),
    
    // 网络错误
    NETWORK_ERROR("NETWORK_ERROR", "网络连接失败"),
    TIMEOUT_ERROR("TIMEOUT_ERROR", "请求超时"),
    
    // 业务逻辑错误
    BUSINESS_ERROR("BUSINESS_ERROR", "业务逻辑错误"),
    INSUFFICIENT_PERMISSIONS("INSUFFICIENT_PERMISSIONS", "权限不足"),
    RESOURCE_EXHAUSTED("RESOURCE_EXHAUSTED", "资源耗尽"),
    
    // 系统错误
    INTERNAL_ERROR("INTERNAL_ERROR", "内部服务器错误"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "服务不可用"),
    
    // 外部服务错误
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "外部服务调用失败"),
    
    // 未知错误
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误");
    
    private final String code;
    private final String message;
    private final Map<String, Object> context;
    
    AppError(String code, String message) {
        this.code = code;
        this.message = message;
        this.context = new HashMap<>();
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }
    
    /**
     * 创建带有上下文的错误
     */
    public AppError withContext(String key, Object value) {
        AppError error = this;
        error.context.put(key, value);
        return error;
    }
    
    /**
     * 创建带有详细消息的错误
     */
    public AppError withMessage(String detailedMessage) {
        return withContext("detailed_message", detailedMessage);
    }
    
    /**
     * 创建带有异常信息的错误
     */
    public AppError withException(Throwable throwable) {
        return withContext("exception", throwable.getMessage())
                .withContext("exception_type", throwable.getClass().getSimpleName());
    }
    
    /**
     * 获取完整的错误信息
     */
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(code).append("] ").append(message);
        
        if (!context.isEmpty()) {
            sb.append(" - Context: ");
            context.forEach((key, value) -> 
                sb.append(key).append("=").append(value).append(", "));
            // 移除最后的逗号和空格
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getFullMessage();
    }
    
    /**
     * 从异常创建错误
     */
    public static AppError fromException(Throwable throwable) {
        if (throwable instanceof IllegalArgumentException) {
            return VALIDATION_ERROR.withException(throwable);
        } else if (throwable instanceof RuntimeException) {
            return INTERNAL_ERROR.withException(throwable);
        } else {
            return UNKNOWN_ERROR.withException(throwable);
        }
    }
    
    /**
     * 从错误代码创建错误
     */
    public static AppError fromCode(String code) {
        for (AppError error : values()) {
            if (error.code.equals(code)) {
                return error;
            }
        }
        return UNKNOWN_ERROR.withContext("unknown_code", code);
    }
}
