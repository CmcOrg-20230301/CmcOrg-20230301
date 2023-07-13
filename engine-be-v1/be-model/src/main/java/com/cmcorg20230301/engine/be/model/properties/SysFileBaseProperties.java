package com.cmcorg20230301.engine.be.model.properties;

import lombok.Data;

/**
 * 文件系统配置类，基础类
 */
@Data
public class SysFileBaseProperties {

    /**
     * 密钥对 accessKey
     */
    private String accessKey;

    /**
     * 密钥对 secretKey
     */
    private String secretKey;

    /**
     * 上传的端点，例如：oss-rg-china-mainland.aliyuncs.com
     */
    private String uploadEndpoint;

    /**
     * 公开下载的端点，例如：be-public-bucket.oss-rg-china-mainland.aliyuncs.com
     */
    private String publicDownloadEndpoint;

    /**
     * 公开类型的桶名，例如：be-public-bucket
     */
    private String bucketPublicName;

    /**
     * 私有类型的桶名，例如：be-private-bucket
     */
    private String bucketPrivateName;

}
