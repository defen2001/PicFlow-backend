package com.defen.picflowbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求实体类
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3730373513930792108L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

}
