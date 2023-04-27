package com.cmcorg20230301.engine.be.file.base.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.FILE)
@RefreshScope
public class FileProperties {

}
