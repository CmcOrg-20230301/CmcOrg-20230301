package com.cmcorg20230301.be.engine.security.properties;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.COMMON)
@RefreshScope
public class CommonProperties {

    @Schema(description = "平台名称")
    private String platformName = "CmcOrg";

    @Schema(description = "外网：ip/域名")
    private String internetAddress = "127.0.0.1";

    @Schema(description = "生产环境：不处理的 kafka主题集合")
    private Set<String> prodNotHandleKafkaTopSet;

    @Schema(description = "开发环境：不处理的 kafka主题集合")
    private Set<String> devNotHandleKafkaTopSet;

    @Schema(description = "日志推送的 url，备注：如果为空，则不打印，如果为：localhost，则本地日志打印")
    private String logPushUrl;

}
