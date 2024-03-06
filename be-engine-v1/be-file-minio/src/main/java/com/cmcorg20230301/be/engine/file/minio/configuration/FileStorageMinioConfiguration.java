package com.cmcorg20230301.be.engine.file.minio.configuration;

import com.cmcorg20230301.be.engine.file.base.model.configuration.ISysFileStorage;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.be.engine.file.minio.util.FileMinioUtil;
import java.io.InputStream;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

/**
 * minio文件存储相关配置类
 */
@Configuration
public class FileStorageMinioConfiguration implements ISysFileStorage {

    @Override
    public SysFileStorageTypeEnum getSysFileStorageType() {
        return SysFileStorageTypeEnum.MINIO;
    }

    @Override
    public void upload(String bucketName, String objectName, MultipartFile file,
        @NotNull SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        FileMinioUtil.upload(bucketName, objectName, file, sysFileStorageConfigurationDO);
    }

    @Override
    public InputStream download(String bucketName, String objectName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        return FileMinioUtil.download(bucketName, objectName, sysFileStorageConfigurationDO);
    }

    @Override
    public void remove(String bucketName, Set<String> objectNameSet,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        FileMinioUtil.remove(bucketName, objectNameSet, sysFileStorageConfigurationDO);
    }

    @Override
    public String getUrl(String uri, String bucketName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {
        return sysFileStorageConfigurationDO.getPublicDownloadEndpoint() + "/" + bucketName + "/"
            + uri;
    }

}
