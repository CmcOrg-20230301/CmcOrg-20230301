package com.cmcorg20230301.engine.be.sms.tencent.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.TENCENT)
@RefreshScope
public class TencentProperties {

    @Schema(description = "密钥对 secretId")
    private String secretId;

    @Schema(description = "密钥对 secretKey")
    private String secretKey;

    @Schema(description = "短信应用ID")
    private String sdkAppId;

    @Schema(description = "短信签名内容")
    private String signName;

    @Schema(description = "发送：账号注销")
    private String sendDelete;

    @Schema(description = "发送：绑定手机")
    private String sendBind;

    @Schema(description = "发送：修改手机")
    private String sendUpdate;

    @Schema(description = "发送：修改密码")
    private String sendUpdatePassword;

    @Schema(description = "发送：忘记密码")
    private String sendForgetPassword;

    @Schema(description = "发送：登录短信")
    private String sendSignIn;

    @Schema(description = "发送：注册短信")
    private String sendSignUp;

}
