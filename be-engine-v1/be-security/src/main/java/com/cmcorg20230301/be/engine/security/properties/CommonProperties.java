package com.cmcorg20230301.be.engine.security.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

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

    @Schema(description = "单点登录，采用配置的租户主键 id")
    private Long singleSignInTenantId;

}
