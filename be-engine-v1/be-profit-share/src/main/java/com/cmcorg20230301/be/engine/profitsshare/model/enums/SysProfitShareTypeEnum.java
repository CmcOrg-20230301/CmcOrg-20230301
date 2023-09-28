package com.cmcorg20230301.be.engine.profitsshare.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SysProfitShareTypeEnum {

    ALI(101), // 支付宝

    WX(201), // 微信

    UNION(301), // 云闪付

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
