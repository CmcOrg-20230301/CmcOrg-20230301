package com.cmcorg20230301.engine.be.socket.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SOCKET)
@RefreshScope
public class SocketProperties {

    @Schema(description = "短信发送类型：1 腾讯云 2 阿里云")
    private Integer type;

}
