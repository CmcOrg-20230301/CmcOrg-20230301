package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_tenant_ref_user")
@Data
@Schema(description = "关联表：租户，用户")
public class SysTenantRefUserDO {

    @TableId(type = IdType.INPUT)
    @Schema(description = "租户主键 id")
    private Long tenantId;

    @Schema(description = "用户主键 id")
    private Long userId;

}
