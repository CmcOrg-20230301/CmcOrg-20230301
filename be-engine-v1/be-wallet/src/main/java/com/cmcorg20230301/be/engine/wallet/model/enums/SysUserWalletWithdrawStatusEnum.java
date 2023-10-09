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

    COMMIT(101, "待受理"), // 待受理（可取消）

    ACCEPT(201, "受理中"), // 受理中（不可取消）

    SUCCESS(301, "已成功"), // 已成功

    REJECT(401, "已拒绝"), // 已拒绝（需要填写拒绝理由）

    CANCEL(501, "已取消"), // 已取消（用户在待受理的时候，可以取消）

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

    private final String name;

}
