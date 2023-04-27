package com.cmcorg20230301.engine.be.file.minio.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.FILE_MINIO)
@RefreshScope
public class FileMinioProperties {

    @Schema(description = "密钥对 accessKey")
    private String accessKey;

    @Schema(description = "密钥对 secretKey")
    private String secretKey;

    @Schema(description = "访问的端点")
    private String endpoint;

    @Schema(description = "桶名")
    private String bucketName;

}
