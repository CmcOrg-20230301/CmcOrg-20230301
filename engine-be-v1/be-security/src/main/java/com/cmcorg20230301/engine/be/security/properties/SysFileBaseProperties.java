package com.cmcorg20230301.engine.be.security.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件系统配置类，基础类
 */
@Data
public class SysFileBaseProperties {

    @Schema(description = "密钥对 accessKey")
    private String accessKey;

    @Schema(description = "密钥对 secretKey")
    private String secretKey;

    @Schema(description = "上传的端点")
    private String uploadEndpoint;

    @Schema(description = "公开下载的端点")
    private String publicDownloadEndpoint;

    @Schema(description = "公开类型的桶名")
    private String bucketPublicName;

    @Schema(description = "私有类型的桶名")
    private String bucketPrivateName;

}
