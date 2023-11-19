package com.cmcorg20230301.be.engine.pay.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayRefType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关联的类型枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayRefTypeEnum implements ISysPayRefType {

    NONE(101), // 无

    WALLET_RECHARGE_USER(201), // 钱包充值-用户

    WALLET_RECHARGE_TENANT(202), // 钱包充值-租户

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
