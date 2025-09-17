package org.example.rust;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Rust风格的Result类型，用于处理可能成功或失败的操作
 * 类似于Rust的Result<T, E>类型
 */
public abstract class Result<T, E> {
    
    /**
     * 创建成功结果
     */
    public static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }
    
    /**
     * 创建错误结果
     */
    public static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }
    
    /**
     * 检查是否为成功结果
     */
    public abstract boolean isOk();
    
    /**
     * 检查是否为错误结果
     */
    public abstract boolean isErr();
    
    /**
     * 获取成功值，如果为错误则抛出异常
     */
    public abstract T unwrap();
    
    /**
     * 获取成功值，如果为错误则返回默认值
     */
    public abstract T unwrapOr(T defaultValue);
    
    /**
     * 获取成功值，如果为错误则通过函数计算默认值
     */
    public abstract T unwrapOrElse(Supplier<T> defaultSupplier);
    
    /**
     * 获取错误值，如果为成功则抛出异常
     */
    public abstract E unwrapErr();
    
    /**
     * 映射成功值
     */
    public abstract <U> Result<U, E> map(Function<T, U> mapper);
    
    /**
     * 映射错误值
     */
    public abstract <F> Result<T, F> mapErr(Function<E, F> mapper);
    
    /**
     * 扁平化映射
     */
    public abstract <U> Result<U, E> andThen(Function<T, Result<U, E>> mapper);
    
    /**
     * 匹配模式，根据结果类型执行不同操作
     */
    public abstract <R> R match(Function<T, R> okHandler, Function<E, R> errHandler);
    
    /**
     * 成功结果实现
     */
    private static class Ok<T, E> extends Result<T, E> {
        private final T value;
        
        Ok(T value) {
            this.value = value;
        }
        
        @Override
        public boolean isOk() {
            return true;
        }
        
        @Override
        public boolean isErr() {
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
        public E unwrapErr() {
            throw new IllegalStateException("Called unwrapErr on Ok value");
        }
        
        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            try {
                return Result.ok(mapper.apply(value));
            } catch (Exception e) {
                return Result.err((E) e);
            }
        }
        
        @Override
        public <F> Result<T, F> mapErr(Function<E, F> mapper) {
            return Result.ok(value);
        }
        
        @Override
        public <U> Result<U, E> andThen(Function<T, Result<U, E>> mapper) {
            try {
                return mapper.apply(value);
            } catch (Exception e) {
                return Result.err((E) e);
            }
        }
        
        @Override
        public <R> R match(Function<T, R> okHandler, Function<E, R> errHandler) {
            return okHandler.apply(value);
        }
        
        @Override
        public String toString() {
            return "Ok(" + value + ")";
        }
    }
    
    /**
     * 错误结果实现
     */
    private static class Err<T, E> extends Result<T, E> {
        private final E error;
        
        Err(E error) {
            this.error = error;
        }
        
        @Override
        public boolean isOk() {
            return false;
        }
        
        @Override
        public boolean isErr() {
            return true;
        }
        
        @Override
        public T unwrap() {
            throw new IllegalStateException("Called unwrap on Err value: " + error);
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
        public E unwrapErr() {
            return error;
        }
        
        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return Result.err(error);
        }
        
        @Override
        public <F> Result<T, F> mapErr(Function<E, F> mapper) {
            try {
                return Result.err(mapper.apply(error));
            } catch (Exception e) {
                return Result.err((F) e);
            }
        }
        
        @Override
        public <U> Result<U, E> andThen(Function<T, Result<U, E>> mapper) {
            return Result.err(error);
        }
        
        @Override
        public <R> R match(Function<T, R> okHandler, Function<E, R> errHandler) {
            return errHandler.apply(error);
        }
        
        @Override
        public String toString() {
            return "Err(" + error + ")";
        }
    }
}
