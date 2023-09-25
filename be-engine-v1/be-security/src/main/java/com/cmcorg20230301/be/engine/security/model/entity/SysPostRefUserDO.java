package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_post_ref_user")
@Data
@Schema(description = "关联表：岗位，用户")
public class SysPostRefUserDO {

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "租户 id")
    private Long tenantId;

    @TableId(type = IdType.INPUT)
    @Schema(description = "岗位主键 id")
    private Long postId;

    @Schema(description = "用户主键 id")
    private Long userId;

}
