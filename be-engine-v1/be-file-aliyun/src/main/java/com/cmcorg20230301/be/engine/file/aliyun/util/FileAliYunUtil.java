package com.cmcorg20230301.be.engine.file.aliyun.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import lombok.SneakyThrows;

/**
 * 阿里云文件工具类
 */
@Component
public class FileAliYunUtil {

    /**
     * 上传文件 备注：objectName 相同会被覆盖掉
     */
    @SneakyThrows
    public static void upload(String bucketName, String objectName, MultipartFile file,
        @NotNull SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        InputStream inputStream = file.getInputStream();

        OSS oss = new OSSClientBuilder().build(sysFileStorageConfigurationDO.getUploadEndpoint(),
            sysFileStorageConfigurationDO.getAccessKey(), sysFileStorageConfigurationDO.getSecretKey());

        oss.putObject(bucketName, objectName, inputStream);

        IoUtil.close(inputStream);

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    public static InputStream download(String bucketName, String objectName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        OSS oss = new OSSClientBuilder().build(sysFileStorageConfigurationDO.getUploadEndpoint(),
            sysFileStorageConfigurationDO.getAccessKey(), sysFileStorageConfigurationDO.getSecretKey());

        return oss.getObject(bucketName, objectName).getObjectContent();

    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    public static void remove(String bucketName, Set<String> objectNameSet,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        if (CollUtil.isEmpty(objectNameSet)) {
            return;
        }

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);

        deleteObjectsRequest.setKeys(new ArrayList<>(objectNameSet));

        OSS oss = new OSSClientBuilder().build(sysFileStorageConfigurationDO.getUploadEndpoint(),
            sysFileStorageConfigurationDO.getAccessKey(), sysFileStorageConfigurationDO.getSecretKey());

        oss.deleteObject(deleteObjectsRequest);

    }

}
