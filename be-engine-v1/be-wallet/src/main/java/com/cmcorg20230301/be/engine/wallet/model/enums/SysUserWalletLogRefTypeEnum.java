package com.cmcorg20230301.be.engine.wallet.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.wallet.model.interfaces.ISysUserWalletLogRefType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户钱包操作日志，关联的类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysUserWalletLogRefTypeEnum implements ISysUserWalletLogRefType {

    NONE(101), // 无

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
