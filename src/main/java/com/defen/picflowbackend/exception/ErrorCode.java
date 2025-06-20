package com.defen.picflowbackend.exception;

import lombok.Getter;

/**
 * 错误码
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "Success"),
    PARAM_ERROR(400001, "参数校验失败"),
    NOT_LOGIN_ERROR(401000, "用户未登录"),
    UNAUTHORIZED_ERROR(401001, "未授权访问"),
    NOT_FOUND_ERROR(404001, "资源不存在"),
    FORBIDDEN_ERROR(403000, "禁止访问"),
    OPERATION_ERROR(403001, "操作失败"),
    SYSTEM_ERROR(500000, "系统内部错误"),
    DB_ERROR(500001, "数据库操作失败");

    /**
     * 状态吗
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

}