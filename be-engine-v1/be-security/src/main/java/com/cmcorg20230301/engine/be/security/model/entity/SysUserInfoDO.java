package com.cmcorg20230301.engine.be.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_user_info")
@Data
@Schema(description = "子表：用户基本信息，主表：用户")
public class SysUserInfoDO {

    @TableId
    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "该用户的 uuid，本系统使用 id，不使用此字段（uuid）")
    private String uuid;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像 fileId（文件主键 id），备注：没有时则为 -1")
    private Long avatarFileId;

}
