package com.defen.picflowbackend.exception;

import org.springframework.validation.BindException;

/**
 * 异常处理工具类
 */
public class ExceptionUtils {

    /**
     * 如果条件为 true，抛出指定异常
     *
     * @param condition 条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 如果条件为 true，抛出指定异常
     *
     * @param condition 条件
     * @param errorCode 错误码枚举
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 如果条件为 true，抛出指定异常（自定义消息）
     *
     * @param condition 条件
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }

    /**
     * 如果对象为 null，抛出指定异常
     *
     * @param object    对象
     * @param errorCode 错误码枚举
     */
    public static <T> T throwIfNull(T object, ErrorCode errorCode) {
        throwIf(object == null, new BusinessException(errorCode));
        return object;
    }
}