package com.defen.picflowbackend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录信息视图实体类
 */
@Data
public class UserTokenVo implements Serializable {

    private static final long serialVersionUID = -6043786962851194683L;
    /**
     * id
     */
    private Long id;

    /**
     * token
     */
    private String token;


    /**
     * 用户账号
     */
    private String userAccount;


    /**
     * 用户角色：user/admin
     */
    private String userRole;

}
