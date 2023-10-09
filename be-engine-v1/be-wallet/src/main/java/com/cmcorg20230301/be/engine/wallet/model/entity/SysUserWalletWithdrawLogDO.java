package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysOpenBankNameEnum;
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

    @Schema(description = "冗余字段：卡号")
    private String bankCardNo;

    @Schema(description = "冗余字段：开户行")
    private SysOpenBankNameEnum openBankName;

    @Schema(description = "冗余字段：支行")
    private String branchBankName;

    @Schema(description = "冗余字段：收款人姓名")
    private String payeeName;

    @Schema(description = "提现状态")
    private SysUserWalletWithdrawStatusEnum withdrawStatus;

    @Schema(description = "拒绝理由")
    private String rejectReason;

}
