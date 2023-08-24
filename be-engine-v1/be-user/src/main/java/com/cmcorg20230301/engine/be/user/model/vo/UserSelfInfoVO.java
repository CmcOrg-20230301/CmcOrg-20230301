package com.cmcorg20230301.engine.be.user.model.vo;

import com.cmcorg20230301.engine.be.user.model.dto.UserSelfUpdateInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSelfInfoVO extends UserSelfUpdateInfoDTO {

    @Schema(description = "邮箱，会脱敏")
    private String email;

    @Schema(description = "是否有密码，用于前端显示，修改密码/设置密码")
    private Boolean passwordFlag;

    @Schema(description = "登录名，会脱敏")
    private String signInName;

    @Schema(description = "手机号码，会脱敏")
    private String phone;

    @Schema(description = "微信 openId，会脱敏")
    private String wxOpenId;

    @Schema(description = "账号注册时间")
    private Date createTime;

    @Schema(description = "头像 fileId（文件主键 id）")
    private Long avatarFileId;

}
