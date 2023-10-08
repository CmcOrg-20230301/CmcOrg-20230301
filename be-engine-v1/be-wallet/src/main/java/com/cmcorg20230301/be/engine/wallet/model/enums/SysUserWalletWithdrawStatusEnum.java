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

    DRAFT(101, "草稿"), // 草稿（只有草稿状态才可以编辑和删除）

    COMMIT(201, "待受理"), // 待受理（可取消为草稿）

    ACCEPT(301, "受理中"), // 受理中（不可取消为草稿）

    SUCCESS(401, "已成功"), // 已成功

    REJECT(501, "已拒绝"), // 已拒绝（需要填写拒绝理由）

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

    private final String name;

}
