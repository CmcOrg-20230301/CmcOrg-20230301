package com.cmcorg20230301.be.engine.security.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.FILE_TYPE)
@RefreshScope
public class FileTypeProperties {

    /**
     * 如果识别不出来文件类型时，允许的文件名后缀
     */
    private Set<String> allowSet;

}
