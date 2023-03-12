package com.cmcorg20230301.engine.be.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

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

    @Schema(description = "头像 uri，例如：阿里云oss文件访问 uri，文件访问 uri")
    private String avatarUri;

    @Schema(description = "头像 fileId（文件主键 id）")
    private Long avatarFileId;

    @Schema(description = "过期时间（年月日时分秒），备注：获取时发现过期了，会重新获取，-1表示永久")
    private Date avatarUriExpireTime;

}
