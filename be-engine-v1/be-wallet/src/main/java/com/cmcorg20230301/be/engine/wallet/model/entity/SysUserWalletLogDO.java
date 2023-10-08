package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_wallet_log")
@Data
@Schema(description = "主表：用户钱包操作日志")
public class SysUserWalletLogDO extends BaseEntity {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "记录名")
    private String name;

    @Schema(description = "记录类型：101 增加 201 减少")
    private SysUserWalletLogTypeEnum type;

    @Schema(description = "总金额，前")
    private BigDecimal totalMoneyPre;

    @Schema(description = "总金额，变")
    private BigDecimal totalMoneyChange;

    @Schema(description = "总金额，后")
    private BigDecimal totalMoneySuf;

    @Schema(description = "可提现的钱，前")
    private BigDecimal withdrawableMoneyPre;

    @Schema(description = "可提现的钱，变")
    private BigDecimal withdrawableMoneyChange;

    @Schema(description = "可提现的钱，后")
    private BigDecimal withdrawableMoneySuf;

}
