package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@TableName(value = "sys_user_single_sign_in")
@Data
@Schema(description = "子表：用户统一登录，主表：用户")
public class SysUserSingleSignInDO {

    @Schema(description = "用户主键 id")
    @TableId(type = IdType.INPUT)
    private Long id;

    @Schema(description = "租户 id")
    private Long tenantId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "统一登录微信 appId，可以为空")
    private String wxAppId;

    @Schema(description = "统一登录微信 openId，可以为空")
    private String wxOpenId;

    @Schema(description = "统一登录手机号，可以为空")
    private String phone;

    @Schema(description = "统一登录邮箱，可以为空")
    private String email;

}