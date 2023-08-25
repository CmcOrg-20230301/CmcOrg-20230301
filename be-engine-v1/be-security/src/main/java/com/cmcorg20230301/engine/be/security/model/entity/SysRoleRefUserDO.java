package com.cmcorg20230301.engine.be.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_role_ref_user")
@Data
@Schema(description = "关联表：角色，用户")
public class SysRoleRefUserDO {

    @Schema(description = "租户id")
    private Long tenantId;

    @TableId
    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "用户id")
    private Long userId;

}
