package com.cmcorg20230301.be.engine.file.aliyun.configuration;

import com.cmcorg20230301.be.engine.file.aliyun.properties.FileAliYunProperties;
import com.cmcorg20230301.be.engine.file.aliyun.util.FileAliYunUtil;
import com.cmcorg20230301.be.engine.file.base.model.configuration.ISysFile;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.be.engine.model.properties.SysFileBaseProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Set;

/**
 * 阿里云 oss文件存储相关配置类
 */
@Configuration
public class FileAliConfiguration implements ISysFile {

    @Resource
    FileAliYunProperties fileAliYunProperties;

    @Override
    public SysFileStorageTypeEnum getSysFileStorageType() {
        return SysFileStorageTypeEnum.ALI_YUN;
    }

    @Override
    public void upload(String bucketName, String objectName, MultipartFile file) {
        FileAliYunUtil.upload(bucketName, objectName, file);
    }

    @Override
    public InputStream download(String bucketName, String objectName) {
        return FileAliYunUtil.download(bucketName, objectName);
    }

    @Override
    public void remove(String bucketName, Set<String> objectNameSet) {
        FileAliYunUtil.remove(bucketName, objectNameSet);
    }

    @Override
    public String getUrl(String uri, String bucketName) {
        return fileAliYunProperties.getPublicDownloadEndpoint() + "/" + uri;
    }

    @Override
    public SysFileBaseProperties getSysFileBaseProperties() {
        return fileAliYunProperties;
    }

}
