package com.cmcorg20230301.engine.be.file.base.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.FILE)
@RefreshScope
public class FileProperties {

    @Schema(description = "头像存放位置：1 阿里云 2 minio")
    private Integer avatarStorageType;

}
