package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.wallet.model.interfaces.ISysUserWalletLogType;
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

    /**
     * {@link ISysUserWalletLogType}
     */
    @Schema(description = "记录类型：1开头 增加 2开头 减少")
    private Integer type;

    @Schema(description = "关联的 id")
    private Long refId;

    @Schema(description = "关联的数据")
    private String refData;

    @Schema(description = "可提现的钱，前")
    private BigDecimal withdrawableMoneyPre;

    @Schema(description = "可提现的钱，变")
    private BigDecimal withdrawableMoneyChange;

    @Schema(description = "可提现的钱，后")
    private BigDecimal withdrawableMoneySuf;

    @Schema(description = "可提现的钱，预使用，前")
    private BigDecimal withdrawablePreUseMoneyPre;

    @Schema(description = "可提现的钱，预使用，变")
    private BigDecimal withdrawablePreUseMoneyChange;

    @Schema(description = "可提现的钱，预使用，后")
    private BigDecimal withdrawablePreUseMoneySuf;

}
