package com.cmcorg20230301.be.engine.file.aliyun.configuration;

import com.cmcorg20230301.be.engine.file.aliyun.util.FileAliYunUtil;
import com.cmcorg20230301.be.engine.file.base.model.configuration.ISysFileStorage;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileStorageTypeEnum;
import java.io.InputStream;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云 oss文件存储相关配置类
 */
@Configuration
public class FileStorageAliConfiguration implements ISysFileStorage {

    @Override
    public SysFileStorageTypeEnum getSysFileStorageType() {
        return SysFileStorageTypeEnum.ALI_YUN;
    }

    @Override
    public void upload(String bucketName, String objectName, MultipartFile file,
        @NotNull SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        FileAliYunUtil.upload(bucketName, objectName, file, sysFileStorageConfigurationDO);
    }

    @Override
    public InputStream download(String bucketName, String objectName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        return FileAliYunUtil.download(bucketName, objectName, sysFileStorageConfigurationDO);
    }

    @Override
    public void remove(String bucketName, Set<String> objectNameSet,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        FileAliYunUtil.remove(bucketName, objectNameSet, sysFileStorageConfigurationDO);
    }

    @Override
    public String getUrl(String uri, String bucketName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        return sysFileStorageConfigurationDO.getPublicDownloadEndpoint() + "/" + uri;
    }

}
