package com.cmcorg20230301.engine.be.sms.base.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SMS)
@RefreshScope
public class SmsProperties {

    @Schema(description = "短信发送类型：1 腾讯云 2 阿里云")
    private Integer type;

}
