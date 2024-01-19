package com.cmcorg20230301.be.engine.sms.base.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.sms.base.model.interfaces.ISysSmsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysSmsConfigurationInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @Schema(description = "是否是默认短信发送，备注：只会有一个默认短信发送")
    private Boolean defaultFlag;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

    /**
     * {@link ISysSmsType}
     */
    @NotNull
    @Schema(description = "短信类型：101 阿里 201 腾讯")
    private Integer type;

    @NotBlank
    @Schema(description = "短信名")
    private String name;

    @Schema(description = "钥匙")
    private String secretId;

    @Schema(description = "秘钥")
    private String secretKey;

    @Schema(description = "短信应用 id")
    private String sdkAppId;

    @Schema(description = "签名内容")
    private String signName;

    @Schema(description = "发送：通用短信")
    private String sendCommon;

    @Schema(description = "发送：注册短信")
    private String sendSignUp;

    @Schema(description = "发送：登录短信")
    private String sendSignIn;

    @Schema(description = "发送：设置密码")
    private String sendSetPassword;

    @Schema(description = "发送：修改密码")
    private String sendUpdatePassword;

    @Schema(description = "发送：设置登录名")
    private String sendSetSignInName;

    @Schema(description = "发送：修改登录名")
    private String sendUpdateSignInName;

    @Schema(description = "发送：设置邮箱")
    private String sendSetEmail;

    @Schema(description = "发送：修改邮箱")
    private String sendUpdateEmail;

    @Schema(description = "发送：设置微信")
    private String sendSetWx;

    @Schema(description = "发送：修改微信")
    private String sendUpdateWx;

    @Schema(description = "发送：设置手机")
    private String sendSetPhone;

    @Schema(description = "发送：修改手机")
    private String sendUpdatePhone;

    @Schema(description = "发送：忘记密码")
    private String sendForgetPassword;

    @Schema(description = "发送：账号注销")
    private String sendSignDelete;

}
