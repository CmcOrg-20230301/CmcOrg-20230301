package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    @TableField(exist = false)
    @Schema(description = "上级 id，用于：租户钱包列表的树形结构展示，没有其他用途")
    private Long parentId;

    @TableField(exist = false)
    @Schema(description = "为了：组装树结构，没有其他用途")
    private Integer orderNo;

    @Schema(description = "可提现的钱")
    private BigDecimal withdrawableMoney;

    @Schema(description = "可提现的钱：预使用，例如用于：用户充值时，需要扣除租户的可提现的钱时")
    private BigDecimal withdrawablePreUseMoney;

    @TableField(exist = false)
    @Schema(description = "总金额")
    private BigDecimal totalMoney;

    public BigDecimal getTotalMoney() {
        return withdrawableMoney;
    }

    @TableField(exist = false)
    @Schema(description = "实际可提现的钱")
    private BigDecimal withdrawableRealMoney;

    public BigDecimal getWithdrawableRealMoney() {
        return withdrawableMoney.subtract(withdrawablePreUseMoney);
    }
}
