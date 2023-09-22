package com.cmcorg20230301.be.engine.sign.helper.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方应用类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum SysOtherAppTypeEnum {

    WX_MINI_PROGRAM(101), // 微信小程序

    WX_PUBLIC_ACCOUNT(102), // 微信公众号

    ALI_PAY_PROGRAM(201), // 支付宝小程序

    ;

    @EnumValue
    @JsonValue
    private final int code;

}