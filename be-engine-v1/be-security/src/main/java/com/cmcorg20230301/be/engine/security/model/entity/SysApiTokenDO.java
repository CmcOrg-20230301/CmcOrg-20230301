package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

    @Schema(description = "")
    private Date createTime;

    @Schema(description = "")
    private Date updateTime;

    @Schema(description = "调用 api时，传递的 token，格式：uuid")
    private String token;

    @Schema(description = "apiToken名")
    private String name;

}