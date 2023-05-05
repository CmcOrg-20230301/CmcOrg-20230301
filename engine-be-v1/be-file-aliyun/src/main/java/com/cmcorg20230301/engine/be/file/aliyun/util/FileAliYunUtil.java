package com.cmcorg20230301.engine.be.file.aliyun.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.DeleteObjectsRequest;
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

    public FileAliYunUtil(OSS oss) {

        FileAliYunUtil.oss = oss;

    }

    /**
     * 上传文件
     * 备注：objectName 相同会被覆盖掉
     */
    @SneakyThrows
    public static void upload(String bucketName, String objectName, MultipartFile file) {

        InputStream inputStream = file.getInputStream();

        oss.putObject(bucketName, objectName, inputStream);

        IoUtil.close(inputStream);

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    public static InputStream download(String bucketName, String objectName) {

        return oss.getObject(bucketName, objectName).getObjectContent();

    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    public static void remove(String bucketName, Set<String> objectNameSet) {

        if (CollUtil.isEmpty(objectNameSet)) {
            return;
        }

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.setKeys(new ArrayList<>(objectNameSet));

        oss.deleteObject(deleteObjectsRequest);

    }

}
