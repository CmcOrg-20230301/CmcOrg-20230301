package com.cmcorg20230301.engine.be.file.aliyun.util;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.cmcorg20230301.engine.be.file.aliyun.properties.FileAliYunProperties;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

/**
 * 阿里云文件工具类
 */
@Component
public class FileAliYunUtil {

    private static OSS oss;
    private static FileAliYunProperties fileAliYunProperties;

    public FileAliYunUtil(OSS oss, FileAliYunProperties fileAliYunProperties) {

        FileAliYunUtil.oss = oss;
        FileAliYunUtil.fileAliYunProperties = fileAliYunProperties;

    }

    private static String getBucketName(String bucketName) {

        if (StrUtil.isBlank(bucketName)) {
            bucketName = fileAliYunProperties.getBucketPublicName();
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

        PutObjectResult putObjectResult = oss.putObject(bucketName, objectName, file.getInputStream());

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    public static InputStream download(@Nullable String bucketName, String objectName) {

        bucketName = getBucketName(bucketName);

        return oss.getObject(bucketName, objectName).getObjectContent();

    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    public static void remove(@Nullable String bucketName, Set<String> objectNameSet) {

        bucketName = getBucketName(bucketName);

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.setKeys(new ArrayList<>(objectNameSet));

        oss.deleteObject(deleteObjectsRequest);

    }

}
