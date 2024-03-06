package com.cmcorg20230301.be.engine.file.minio.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import java.util.Set;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * minio文件工具类
 */
@Component
public class FileMinioUtil {

    /**
     * 上传文件 备注：objectName 相同会被覆盖掉
     */
    @SneakyThrows
    public static void upload(String bucketName, String objectName, MultipartFile file,
        @NotNull SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        InputStream inputStream = file.getInputStream();

        MinioClient minioClient = MinioClient.builder()
            .endpoint(sysFileStorageConfigurationDO.getUploadEndpoint())
            .credentials(sysFileStorageConfigurationDO.getAccessKey(),
                sysFileStorageConfigurationDO.getSecretKey())
            .build();

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
            .stream(inputStream, -1, ObjectWriteArgs.MAX_PART_SIZE).build());

        IoUtil.close(inputStream);

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    public static InputStream download(String bucketName, String objectName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        MinioClient minioClient = MinioClient.builder()
            .endpoint(sysFileStorageConfigurationDO.getUploadEndpoint())
            .credentials(sysFileStorageConfigurationDO.getAccessKey(),
                sysFileStorageConfigurationDO.getSecretKey())
            .build();

        return minioClient.getObject(
            GetObjectArgs.builder().bucket(bucketName).object(objectName).build());

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

        MinioClient minioClient = MinioClient.builder()
            .endpoint(sysFileStorageConfigurationDO.getUploadEndpoint())
            .credentials(sysFileStorageConfigurationDO.getAccessKey(),
                sysFileStorageConfigurationDO.getSecretKey())
            .build();

        for (String item : objectNameSet) {

            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucketName).object(item).build());

        }

    }

}
