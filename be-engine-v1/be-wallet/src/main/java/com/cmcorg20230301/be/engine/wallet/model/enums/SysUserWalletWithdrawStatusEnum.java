package com.cmcorg20230301.be.engine.wallet.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户钱包提现状态，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysUserWalletWithdrawStatusEnum {

    PENDING(101), // 待处理

    SUCCESS(201), // 已成功

    REJECT(301), // 已拒绝

    CANCEL(401), // 已取消

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
