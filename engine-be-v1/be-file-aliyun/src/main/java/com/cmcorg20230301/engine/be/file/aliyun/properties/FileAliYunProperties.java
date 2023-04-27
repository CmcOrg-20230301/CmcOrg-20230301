package com.cmcorg20230301.engine.be.file.aliyun.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.FILE_ALI_YUN)
@RefreshScope
public class FileAliYunProperties {

    @Schema(description = "密钥对 accessKeyId")
    private String accessKeyId;

    @Schema(description = "密钥对 accessKeySecret")
    private String accessKeySecret;

    @Schema(description = "访问的端点")
    private String endpoint;

    @Schema(description = "桶名")
    private String bucketName;

}
