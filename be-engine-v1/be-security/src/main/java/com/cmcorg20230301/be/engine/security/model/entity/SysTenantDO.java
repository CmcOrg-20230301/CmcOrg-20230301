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

}
