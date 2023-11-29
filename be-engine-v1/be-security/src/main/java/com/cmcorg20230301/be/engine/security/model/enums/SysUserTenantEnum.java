package com.cmcorg20230301.be.engine.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型：用户/租户
 */
@Getter
@AllArgsConstructor
public enum SysUserTenantEnum {

    USER(1), // 用户

    TENANT(2), // 租户

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
