package com.cmcorg20230301.be.engine.api.token.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "主表：apiToken")
public class SysApiTokenDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "租户 id")
    private Long tenantId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "调用 api时，传递的 token，格式：uuid")
    private String token;

    @Schema(description = "apiToken名")
    private String name;

}