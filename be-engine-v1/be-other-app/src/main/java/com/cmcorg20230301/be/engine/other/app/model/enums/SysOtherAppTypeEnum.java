package com.cmcorg20230301.be.engine.other.app.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppType;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方应用类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum SysOtherAppTypeEnum implements ISysOtherAppType {

    WX_MINI_PROGRAM(101), // 微信小程序

    WX_OFFICIAL_ACCOUNT(102), // 微信公众号

    WX_WORK(103), // 企业微信

    ALI_PAY_PROGRAM(201), // 支付宝小程序

    BAI_DU(301), // 百度

    VOLC_ENGINE(401), // 火山引擎

    MICROSOFT(501), // 微软

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
