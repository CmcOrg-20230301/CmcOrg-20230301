package com.cmcorg20230301.be.engine.pay.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayTypeEnum {

    ALI(101), // 支付宝

    WX(201), // 微信

    UNION(301), // 云闪付

    GOOGLE(401), // 谷歌

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
