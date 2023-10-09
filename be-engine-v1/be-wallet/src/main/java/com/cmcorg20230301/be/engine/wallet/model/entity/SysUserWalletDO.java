package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_wallet")
@Data
@Schema(description = "子表：用户钱包，主表：用户")
public class SysUserWalletDO extends BaseEntityNoId {

    @TableId(type = IdType.INPUT)
    @Schema(description = "用户主键 id")
    private Long id;

    @TableField(value = "SUM( withdrawable_money ) AS totalMoney", property = "totalMoney", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @Schema(description = "总金额")
    private BigDecimal totalMoney;

    @Schema(description = "可提现的钱")
    private BigDecimal withdrawableMoney;

}
