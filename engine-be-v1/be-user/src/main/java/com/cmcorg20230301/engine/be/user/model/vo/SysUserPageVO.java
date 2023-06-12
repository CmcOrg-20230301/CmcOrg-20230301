package com.cmcorg20230301.engine.be.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class SysUserPageVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像uri")
    private String avatarUri;

    @Schema(description = "邮箱，备注：会脱敏")
    private String email;

    @Schema(description = "登录名，会脱敏")
    private String signInName;

    @Schema(description = "手机号码，会脱敏")
    private String phone;

    @Schema(description = "微信 openId，会脱敏")
    private String wxOpenId;

    @Schema(description = "正常/冻结")
    private Boolean enableFlag;

    @Schema(description = "是否有密码")
    private Boolean passwordFlag;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "最近活跃时间")
    private Date lastActiveTime;

    @Schema(description = "角色 idSet")
    private Set<Long> roleIdSet;

}
