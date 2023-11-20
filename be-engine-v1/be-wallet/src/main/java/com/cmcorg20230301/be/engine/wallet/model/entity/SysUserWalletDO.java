package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_wallet")
@Data
@Schema(description = "子表：用户钱包，主表：用户")
public class SysUserWalletDO extends BaseEntityTree<SysUserWalletDO> {

    @TableId(type = IdType.INPUT)
    @Schema(description = "用户主键 id")
    private Long id;

    @TableField(value = "SUM( withdrawable_money + frozenMoney )", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @Schema(description = "总金额")
    private BigDecimal totalMoney;

    @Schema(description = "可提现的钱")
    private BigDecimal withdrawableMoney;

    @Schema(description = "冻结的钱")
    private BigDecimal frozenMoney;

    @TableField(exist = false)
    @Schema(description = "上级 id，用于：租户钱包列表的树形结构展示，没有其他用途")
    private Long parentId;

    @TableField(exist = false)
    @Schema(description = "为了：组装树结构，没有其他用途")
    private Integer orderNo;

}
