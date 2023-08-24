package com.cmcorg20230301.engine.be.file.base.model.configuration;

import com.cmcorg20230301.engine.be.file.base.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.engine.be.model.properties.SysFileBaseProperties;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Set;

public interface ISysFile {

    /**
     * 存放文件的服务器类型
     */
    SysFileStorageTypeEnum getSysFileStorageType();

    /**
     * 上传文件
     * 备注：objectName 相同会被覆盖掉
     */
    void upload(String bucketName, String objectName, MultipartFile file);

    /**
     * 下载文件
     */
    InputStream download(String bucketName, String objectName);

    /**
     * 批量删除文件
     */
    void remove(String bucketName, Set<String> objectNameSet);

    /**
     * 获取：文件预览地址
     *
     * @param uri 例如：avatar/uuid.xxx
     */
    String getUrl(String uri);

    /**
     * 获取：配置的数据
     */
    SysFileBaseProperties getSysFileBaseProperties();

}
