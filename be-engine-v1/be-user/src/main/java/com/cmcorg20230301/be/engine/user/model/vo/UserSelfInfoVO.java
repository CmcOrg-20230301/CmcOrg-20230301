package com.cmcorg20230301.be.engine.user.model.vo;

import com.cmcorg20230301.be.engine.user.model.dto.UserSelfUpdateInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSelfInfoVO extends UserSelfUpdateInfoDTO {

    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "租户 id，可以为空，为空则表示：默认租户：0")
    private Long tenantId;

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

    @Schema(description = "微信 appId，会脱敏")
    private String wxAppId;

    @Schema(description = "账号注册时间")
    private Date createTime;

    @Schema(description = "头像 fileId（文件主键 id）")
    private Long avatarFileId;

    @Schema(description = "是否设置了：统一登录：微信")
    private Boolean singleSignInWxFlag;

    @Schema(description = "是否设置了：统一登录：手机")
    private Boolean singleSignInPhoneFlag;

}
