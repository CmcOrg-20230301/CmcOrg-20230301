package com.cmcorg20230301.be.engine.wallet.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.wallet.model.interfaces.ISysUserWalletLogType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户钱包操作日志类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysUserWalletLogTypeEnum implements ISysUserWalletLogType {

    ADD_PAY(101, "支付充值"), //

    ADD_BACKGROUND(102, "后台充值"), //

    ADD_TIME_CHECK(103, "超时返还"), //

    REDUCE_WITHDRAW(201, "用户提现"), //

    REDUCE_BACKGROUND(202, "后台扣除"), //

    REDUCE_USER_BUY(203, "用户购买"), // 这个购买的意思是：用户购买租户的钱包余额，租户扣除时的变化

    REDUCE_TENANT_BUY(204, "租户购买"), // 这个购买的意思是：租户购买租户的钱包余额，租户扣除时的变化

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

    private final String name;

}
