package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@TableName(value = "sys_user_info")
@Data
@Schema(description = "子表：用户基本信息，主表：用户")
public class SysUserInfoDO {

    @TableId(type = IdType.INPUT)
    @Schema(description = "用户主键 id")
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "租户 id")
    private Long tenantId;

    @Schema(description = "冗余字段：创建时间")
    private Date createTime;

    @Schema(description = "冗余字段：正常/冻结")
    private Boolean enableFlag;

    @Schema(description = "冗余字段：是否注销")
    private Boolean delFlag;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "该用户的 uuid，本系统使用 id，不使用此字段（uuid），备注：不允许修改")
    private String uuid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像 fileId（文件主键 id），备注：没有时则为 -1")
    private Long avatarFileId;

    @Schema(description = "注册终端")
    private SysRequestCategoryEnum signUpType;

    @Schema(description = "最近活跃时间")
    private Date lastActiveTime;

    @Schema(description = "最近 ip")
    private String lastIp;

    @Schema(description = "最近 ip所处区域")
    private String lastRegion;

}
