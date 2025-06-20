package com.defen.picflowbackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    /**
     * 普通用户
     */
    USER("user", "普通用户"),

    /**
     * 管理员
     */
    ADMIN("admin", "管理员");


    private final String value; // 角色值
    private final String label; // 角色描述

    UserRoleEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * 根据值获取枚举
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的用户角色: " + value);
    }
}