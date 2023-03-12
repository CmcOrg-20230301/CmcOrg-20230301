package com.cmcorg20230301.engine.be.wx.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.WX)
public class WxProperties {

    @Schema(description = "appId")
    private String appId;

    @Schema(description = "secret")
    private String secret;

}
