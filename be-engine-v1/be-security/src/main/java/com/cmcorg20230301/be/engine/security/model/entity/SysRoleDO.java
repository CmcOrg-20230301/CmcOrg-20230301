package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_role")
@Data
@Schema(description = "主表：角色")
public class SysRoleDO extends BaseEntity {

    @Schema(description = "角色名（不能重复）")
    private String name;

    @Schema(description = "是否是默认角色，备注：只会有一个默认角色")
    private Boolean defaultFlag;

}
