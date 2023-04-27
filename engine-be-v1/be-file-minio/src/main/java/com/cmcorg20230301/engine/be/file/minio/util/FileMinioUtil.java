package com.cmcorg20230301.engine.be.file.minio.util;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.file.minio.properties.FileMinioProperties;
import com.cmcorg20230301.engine.be.util.util.MyMapUtil;
import io.minio.*;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * minio文件工具类
 */
@Component
public class FileMinioUtil {

    private static MinioClient minioClient;
    private static FileMinioProperties fileMinioProperties;

    public FileMinioUtil(MinioClient minioClient, FileMinioProperties fileMinioProperties) {

        FileMinioUtil.minioClient = minioClient;
        FileMinioUtil.fileMinioProperties = fileMinioProperties;

    }

    private static String getBucketName(String bucketName) {

        if (StrUtil.isBlank(bucketName)) {
            bucketName = fileMinioProperties.getBucketPublicName();
        }

        return bucketName;

    }

    /**
     * 上传文件
     * 备注：objectName 相同会被覆盖掉
     */
    @SneakyThrows
    public static void upload(@Nullable String bucketName, String objectName, MultipartFile file) {

        bucketName = getBucketName(bucketName);

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
            .stream(file.getInputStream(), -1, ObjectWriteArgs.MAX_PART_SIZE).build());

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    public static InputStream download(@Nullable String bucketName, String objectName) {

        bucketName = getBucketName(bucketName);

        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());

    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    public static void remove(@Nullable String bucketName, Set<String> objectNameSet) {

        bucketName = getBucketName(bucketName);

        List<DeleteObject> objectList = new ArrayList<>(MyMapUtil.getInitialCapacity(objectNameSet.size()));

        for (String item : objectNameSet) {

            objectList.add(new DeleteObject(item));

        }

        minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objectList).build());

    }

}
