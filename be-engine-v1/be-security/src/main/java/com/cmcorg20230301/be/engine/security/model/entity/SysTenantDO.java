package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_tenant")
@Data
@Schema(description = "主表：租户")
public class SysTenantDO extends BaseEntityTree<SysTenantDO> {

    @Schema(description = "租户名")
    private String name;

    @Schema(description = "父节点id（顶级则为0）")
    private Long parentId;

    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @TableField(exist = false)
    @Schema(description = "关联菜单的数量")
    private Long refMenuCount;

    @TableField(exist = false)
    @Schema(description = "用户数量")
    private Long userCount;

    @TableField(exist = false)
    @Schema(description = "字典数量")
    private Long dictCount;

    @TableField(exist = false)
    @Schema(description = "参数数量")
    private Long paramCount;

    @Schema(description = "是否独立支付，备注：例如用户在充值钱包的钱时，如果为否，则会扣除租户的钱包余额，如果为是，则不会扣除租户的钱包余额")
    private Boolean independentPayFlag;

}
