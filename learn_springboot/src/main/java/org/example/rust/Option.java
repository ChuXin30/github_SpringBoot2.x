package org.example.rust;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Rust风格的Option类型，用于处理可能为空的值
 * 类似于Rust的Option<T>类型
 */
public abstract class Option<T> {
    
    /**
     * 创建有值的Option
     */
    public static <T> Option<T> some(T value) {
        if (value == null) {
            return none();
        }
        return new Some<>(value);
    }
    
    /**
     * 创建空值Option
     */
    public static <T> Option<T> none() {
        return new None<>();
    }
    
    /**
     * 从可能为null的值创建Option
     */
    public static <T> Option<T> fromNullable(T value) {
        return value == null ? none() : some(value);
    }
    
    /**
     * 检查是否有值
     */
    public abstract boolean isSome();
    
    /**
     * 检查是否为空
     */
    public abstract boolean isNone();
    
    /**
     * 获取值，如果为空则抛出异常
     */
    public abstract T unwrap();
    
    /**
     * 获取值，如果为空则返回默认值
     */
    public abstract T unwrapOr(T defaultValue);
    
    /**
     * 获取值，如果为空则通过函数计算默认值
     */
    public abstract T unwrapOrElse(Supplier<T> defaultSupplier);
    
    /**
     * 映射值
     */
    public abstract <U> Option<U> map(Function<T, U> mapper);
    
    /**
     * 扁平化映射
     */
    public abstract <U> Option<U> andThen(Function<T, Option<U>> mapper);
    
    /**
     * 过滤值
     */
    public abstract Option<T> filter(Function<T, Boolean> predicate);
    
    /**
     * 匹配模式，根据是否有值执行不同操作
     */
    public abstract <R> R match(Function<T, R> someHandler, Supplier<R> noneHandler);
    
    /**
     * 转换为Result
     */
    public <E> Result<T, E> okOr(E error) {
        return match(
            Result::ok,
            () -> Result.err(error)
        );
    }
    
    /**
     * 转换为Result，通过函数计算错误
     */
    public <E> Result<T, E> okOrElse(Supplier<E> errorSupplier) {
        return match(
            Result::ok,
            () -> Result.err(errorSupplier.get())
        );
    }
    
    /**
     * 有值实现
     */
    private static class Some<T> extends Option<T> {
        private final T value;
        
        Some(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isSome() {
            return true;
        }
        
        @Override
        public boolean isNone() {
            return false;
        }
        
        @Override
        public T unwrap() {
            return value;
        }
        
        @Override
        public T unwrapOr(T defaultValue) {
            return value;
        }
        
        @Override
        public T unwrapOrElse(Supplier<T> defaultSupplier) {
            return value;
        }
        
        @Override
        public <U> Option<U> map(Function<T, U> mapper) {
            try {
                U result = mapper.apply(value);
                return fromNullable(result);
            } catch (Exception e) {
                return none();
            }
        }
        
        @Override
        public <U> Option<U> andThen(Function<T, Option<U>> mapper) {
            try {
                return mapper.apply(value);
            } catch (Exception e) {
                return none();
            }
        }
        
        @Override
        public Option<T> filter(Function<T, Boolean> predicate) {
            try {
                return predicate.apply(value) ? this : none();
            } catch (Exception e) {
                return none();
            }
        }
        
        @Override
        public <R> R match(Function<T, R> someHandler, Supplier<R> noneHandler) {
            return someHandler.apply(value);
        }
        
        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }
    
    /**
     * 空值实现
     */
    private static class None<T> extends Option<T> {
        
        @Override
        public boolean isSome() {
            return false;
        }
        
        @Override
        public boolean isNone() {
            return true;
        }
        
        @Override
        public T unwrap() {
            throw new IllegalStateException("Called unwrap on None value");
        }
        
        @Override
        public T unwrapOr(T defaultValue) {
            return defaultValue;
        }
        
        @Override
        public T unwrapOrElse(Supplier<T> defaultSupplier) {
            return defaultSupplier.get();
        }
        
        @Override
        public <U> Option<U> map(Function<T, U> mapper) {
            return none();
        }
        
        @Override
        public <U> Option<U> andThen(Function<T, Option<U>> mapper) {
            return none();
        }
        
        @Override
        public Option<T> filter(Function<T, Boolean> predicate) {
            return none();
        }
        
        @Override
        public <R> R match(Function<T, R> someHandler, Supplier<R> noneHandler) {
            return noneHandler.get();
        }
        
        @Override
        public String toString() {
            return "None";
        }
    }
}
