package com.cmcorg20230301.engine.be.aliyun.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.ALI_YUN)
public class AliYunProperties {

    @Schema(description = "密钥对 accessKeyId")
    private String accessKeyId;

    @Schema(description = "密钥对 accessKeySecret")
    private String accessKeySecret;

    @Schema(description = "签名")
    private String signName;

    @Schema(description = "模板 code")
    private String templateCode;

    @Schema(description = "发送：账号注销")
    private String sendDelete = "1391013";

    @Schema(description = "发送：绑定手机")
    private String sendBind = "1389707";

    @Schema(description = "发送：修改手机")
    private String sendUpdate = "1389628";

    @Schema(description = "发送：修改密码")
    private String sendUpdatePassword = "1381852";

    @Schema(description = "发送：忘记密码")
    private String sendForgetPassword = "1381647";

    @Schema(description = "发送：登录短信")
    private String sendSignIn = "1381644";

    @Schema(description = "发送：注册短信")
    private String sendSignUp = "1380202";

}
