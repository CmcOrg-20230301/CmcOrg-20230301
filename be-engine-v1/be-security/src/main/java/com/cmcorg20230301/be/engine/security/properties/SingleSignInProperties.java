package com.cmcorg20230301.be.engine.security.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SINGLE_SIGN_IN)
@RefreshScope
public class SingleSignInProperties {

    @Schema(description = "微信扫码统一登录的 sys_other_app表主键 id")
    private Long wxSysOtherAppId;

    @Schema(description = "短信统一登录的 sys_sms_configuration表主键 id")
    private Long smsConfigurationId;

    @Schema(description = "邮箱统一登录的 sys_email_configuration表主键 id")
    private Long emailConfigurationId;

}
