package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_wallet_withdraw_log")
@Data
@Schema(description = "主表：用户钱包提现记录")
public class SysUserWalletWithdrawLogDO extends BaseEntity {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "提现金额")
    private BigDecimal withdrawMoney;

    @Schema(description = "银行")
    private String bankName;

    @Schema(description = "户名")
    private String accountName;

    @Schema(description = "卡号")
    private String bankCardNo;

    @Schema(description = "开户行")
    private String openBankName;

    @Schema(description = "提现状态")
    private SysUserWalletWithdrawStatusEnum withdrawStatus;

}
