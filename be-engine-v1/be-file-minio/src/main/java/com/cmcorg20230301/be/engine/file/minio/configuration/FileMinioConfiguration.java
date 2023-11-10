package com.cmcorg20230301.be.engine.file.minio.configuration;

import com.cmcorg20230301.be.engine.file.base.model.configuration.ISysFile;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.be.engine.file.minio.properties.FileMinioProperties;
import com.cmcorg20230301.be.engine.file.minio.util.FileMinioUtil;
import com.cmcorg20230301.be.engine.model.properties.SysFileBaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Set;

/**
 * minio文件存储相关配置类
 */
@Configuration
public class FileMinioConfiguration implements ISysFile {

    @Resource
    FileMinioProperties fileMinioProperties;

    @Override
    public SysFileStorageTypeEnum getSysFileStorageType() {
        return SysFileStorageTypeEnum.MINIO;
    }

    @Override
    public void upload(String bucketName, String objectName, MultipartFile file) {
        FileMinioUtil.upload(bucketName, objectName, file);
    }

    @Override
    public InputStream download(String bucketName, String objectName) {
        return FileMinioUtil.download(bucketName, objectName);
    }

    @Override
    public void remove(String bucketName, Set<String> objectNameSet) {
        FileMinioUtil.remove(bucketName, objectNameSet);
    }

    @Override
    public String getUrl(String uri, String bucketName) {
        return fileMinioProperties.getPublicDownloadEndpoint() + "/" + bucketName + "/" + uri;
    }

    @Override
    public SysFileBaseProperties getSysFileBaseProperties() {
        return fileMinioProperties;
    }

}
