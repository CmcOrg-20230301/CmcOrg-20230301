package com.cmcorg20230301.engine.be.file.base.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.file.aliyun.util.FileAliYunUtil;
import com.cmcorg20230301.engine.be.file.base.properties.FileProperties;
import com.cmcorg20230301.engine.be.file.minio.util.FileMinioUtil;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.engine.be.security.model.enums.SysFileUploadTypeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Set;

/**
 * 文件工具类
 */
@Component
public class FileUtil {

    private static FileProperties fileProperties;

    public FileUtil(FileProperties fileProperties) {

        FileUtil.fileProperties = fileProperties;

    }

    /**
     * 上传文件时的检查
     */
    @NotNull
    public static String uploadCheckWillError(SysFileUploadDTO dto) {

        Assert.notNull(dto.getFile(), "file 不能为空");
        Assert.notNull(dto.getUploadType(), "uploadType 不能为空");

        dto.getUploadType().checkFileSize(dto.getFile()); // 检查：文件大小

        String originalFilename = dto.getFile().getOriginalFilename();

        if (StrUtil.isBlank(originalFilename)) {
            ApiResultVO.error("操作失败：上传的文件名不能为空");
        }

        String fileType = dto.getUploadType().checkFileType(dto.getFile());

        if (fileType == null) {

            ApiResultVO.error("操作失败：暂不支持此文件类型【" + originalFilename + "】，请重新选择");

        }

        return fileType;

    }

    /**
     * 上传文件
     * 备注：objectName 相同会被覆盖掉
     */
    @SneakyThrows
    public static void upload(SysFileUploadDTO dto) {

        // 上传文件时的检查
        String fileType = uploadCheckWillError(dto);

        // 如果是：头像
        if (SysFileUploadTypeEnum.AVATAR.equals(dto.getUploadType())) {

            String folderName = dto.getUploadType().getFolderName();

            Long currentUserId = UserUtil.getCurrentUserId();

            String originalFilename = dto.getFile().getOriginalFilename(); // 旧的文件名

            String newFileName = currentUserId + "." + fileType; // 新的文件名

            String objectName = folderName + "/" + newFileName;

            if (fileProperties.getAvatarStorageType() == 1) { // 头像存放位置：1 阿里云 2 minio

                FileAliYunUtil.upload(null, objectName, dto.getFile());

            } else if (fileProperties.getAvatarStorageType() == 2) {

                FileMinioUtil.upload(null, objectName, dto.getFile());

            }

        }

    }

    /**
     * 下载文件
     */
    @SneakyThrows
    @Nullable
    public static InputStream download(String bucketName, String objectName, HttpServletResponse response) {

        return null;

    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    public static void remove(String bucketName, Set<String> objectNameSet) {

    }

}
