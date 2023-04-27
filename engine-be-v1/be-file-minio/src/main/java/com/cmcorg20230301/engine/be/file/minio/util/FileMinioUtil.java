package com.cmcorg20230301.engine.be.file.minio.util;

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

    public FileMinioUtil(MinioClient minioClient) {

        FileMinioUtil.minioClient = minioClient;

    }

    /**
     * 上传文件
     * 备注：objectName 相同会被覆盖掉
     */
    @SneakyThrows
    private void upload(String bucketName, String objectName, MultipartFile file) {

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
            .stream(file.getInputStream(), -1, ObjectWriteArgs.MAX_PART_SIZE).build());

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    private InputStream download(String bucketName, String objectName) {

        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());

    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    private void remove(String bucketName, Set<String> objectNameSet) {

        List<DeleteObject> objectList = new ArrayList<>(MyMapUtil.getInitialCapacity(objectNameSet.size()));

        for (String item : objectNameSet) {

            objectList.add(new DeleteObject(item));

        }

        minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objectList).build());

    }

}
