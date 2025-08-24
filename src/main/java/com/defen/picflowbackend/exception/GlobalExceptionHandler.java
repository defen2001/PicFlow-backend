package com.defen.picflowbackend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.defen.picflowbackend.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理 sa-token 没登录异常
    @ExceptionHandler(NotLoginException.class)
    public ApiResponse<?> notLoginException(NotLoginException e) {
        log.error("NotLoginException", e);
        return ApiResponse.error(ErrorCode.NOT_LOGIN_ERROR, e.getMessage());
    }

    // 处理 Sa-token 没权限异常
    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException", e);
        return ApiResponse.error(ErrorCode.UNAUTHORIZED_ERROR, e.getMessage());
    }

    // 处理 BusinessException
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    // 处理 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    // 处理其他未捕获的异常
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> exceptionHandler(Exception e) {
        log.error("Unhandled Exception", e);
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, "服务器繁忙，请稍后再试。");
    }
}

