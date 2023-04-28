package com.cmcorg20230301.engine.be.file.aliyun.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import com.cmcorg20230301.engine.be.security.properties.SysFileBaseProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.FILE_ALI_YUN)
@RefreshScope
public class FileAliYunProperties extends SysFileBaseProperties {

}
