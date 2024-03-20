package com.cmcorg20230301.be.engine.security.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_jwt_refresh")
@Data
@Schema(description = "jwt刷新")
public class SysJwtRefreshDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "租户 id")
    private Long tenantId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "格式：uuid")
    private String refreshToken;

}