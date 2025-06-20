package com.defen.picflowbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求实体类
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 4035020301988068568L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String confirmPassword;


}
