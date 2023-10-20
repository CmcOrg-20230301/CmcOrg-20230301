package com.cmcorg20230301.be.engine.wallet.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户钱包提现类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysUserWalletWithdrawTypeEnum {

    USER(1), // 用户

    TENANT(2), // 租户

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
