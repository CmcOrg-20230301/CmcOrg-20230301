package com.cmcorg20230301.be.engine.wallet.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户钱包操作日志类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysUserWalletLogTypeEnum {

    ADD_PAY(101, "支付充值"), //

    ADD_BACKGROUND(102, "后台充值"), //

    REDUCE_WITHDRAW(201, "用户提现"), //

    REDUCE_BACKGROUND(202, "后台扣除"), //

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

    private final String name;

}
