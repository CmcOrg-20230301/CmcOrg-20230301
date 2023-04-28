package com.cmcorg20230301.engine.be.file.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.file.aliyun.properties.FileAliYunProperties;
import com.cmcorg20230301.engine.be.file.aliyun.util.FileAliYunUtil;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFile;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileAuth;
import com.cmcorg20230301.engine.be.file.base.properties.SysFileProperties;
import com.cmcorg20230301.engine.be.file.base.service.SysFileAuthService;
import com.cmcorg20230301.engine.be.file.base.service.SysFileService;
import com.cmcorg20230301.engine.be.file.minio.properties.FileMinioProperties;
import com.cmcorg20230301.engine.be.file.minio.util.FileMinioUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.mysql.util.TransactionUtil;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.engine.be.security.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.engine.be.security.model.enums.SysFileTypeEnum;
import com.cmcorg20230301.engine.be.security.model.enums.SysFileUploadTypeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件工具类
 */
@Component
public class FileUtil {

    private static SysFileProperties sysFileProperties;

    private static SysFileService sysFileService;
    private static SysFileAuthService sysFileAuthService;

    private static FileAliYunProperties fileAliYunProperties;
    private static FileMinioProperties fileMinioProperties;

    public FileUtil(SysFileProperties sysFileProperties, SysFileService sysFileService,
        SysFileAuthService sysFileAuthService, FileAliYunProperties fileAliYunProperties,
        FileMinioProperties fileMinioProperties) {

        FileUtil.sysFileProperties = sysFileProperties;

        FileUtil.sysFileService = sysFileService;
        FileUtil.sysFileAuthService = sysFileAuthService;

        FileUtil.fileAliYunProperties = fileAliYunProperties;
        FileUtil.fileMinioProperties = fileMinioProperties;

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
     * 上传文件：共有和私有
     * 备注：objectName 相同的，会被覆盖掉
     *
     * @return 保存到数据库的，文件主键 id
     */
    @SneakyThrows
    @Nullable
    public static Long upload(SysFileUploadDTO dto) {

        // 上传文件时的检查
        String fileType = uploadCheckWillError(dto);

        Long sysFileId = null;

        // 如果是：头像
        if (SysFileUploadTypeEnum.AVATAR.equals(dto.getUploadType())) {

            // 通用：上传处理
            sysFileId = uploadCommonHandler(dto, fileType, true);

        }

        return sysFileId;

    }

    /**
     * 通用：上传处理
     */
    @NotNull
    private static Long uploadCommonHandler(SysFileUploadDTO dto, String fileType, boolean publicFlag) {

        Long currentUserId = UserUtil.getCurrentUserId();

        String folderName = dto.getUploadType().getFolderName();

        String originalFilename = dto.getFile().getOriginalFilename(); // 旧的文件名

        String newFileName = IdUtil.simpleUUID() + "." + fileType; // 新的文件名

        String objectName = folderName + "/" + newFileName;

        String bucketName = null;

        SysFileStorageTypeEnum storageType = null;

        if (sysFileProperties.getAvatarStorageType() == 1) { // 文件存放位置：1 阿里云 2 minio

            if (publicFlag) {
                bucketName = fileAliYunProperties.getBucketPublicName();
            } else {
                bucketName = fileAliYunProperties.getBucketPrivateName();
            }

            storageType = SysFileStorageTypeEnum.ALI_YUN;

        } else if (sysFileProperties.getAvatarStorageType() == 2) {

            if (publicFlag) {
                bucketName = fileMinioProperties.getBucketPublicName();
            } else {
                bucketName = fileMinioProperties.getBucketPrivateName();
            }

            storageType = SysFileStorageTypeEnum.MINIO;

        }

        if (StrUtil.isBlank(bucketName)) {

            ApiResultVO.error("操作失败：bucketName为空，请联系管理员");

        }

        SysFileStorageTypeEnum finalStorageType = storageType;
        String finalBucketName = bucketName;

        return TransactionUtil.exec(() -> {

            // 通用保存：文件信息到数据库
            Long sysFileId = saveCommonSysFile(dto, fileType, currentUserId, originalFilename, newFileName, objectName,
                finalBucketName, finalStorageType, publicFlag);

            if (sysFileProperties.getAvatarStorageType() == 1) { // 文件存放位置：1 阿里云 2 minio

                FileAliYunUtil.upload(finalBucketName, objectName, dto.getFile());

            } else if (sysFileProperties.getAvatarStorageType() == 2) {

                FileMinioUtil.upload(finalBucketName, objectName, dto.getFile());

            }

            return sysFileId;

        });

    }

    /**
     * 通用保存：文件信息到数据库
     */
    @NotNull
    private static Long saveCommonSysFile(SysFileUploadDTO dto, String fileType, Long currentUserId,
        String originalFilename, String newFileName, String objectName, String bucketName,
        SysFileStorageTypeEnum storageType, boolean publicFlag) {

        SysFile sysFile = new SysFile();

        sysFile.setBelongId(currentUserId);
        sysFile.setBucketName(bucketName);
        sysFile.setUri(objectName);
        sysFile.setOriginFileName(originalFilename);
        sysFile.setNewFileName(newFileName);
        sysFile.setFileExtName(fileType);
        sysFile.setExtraJson("");
        sysFile.setUploadType(dto.getUploadType());
        sysFile.setStorageType(storageType);
        sysFile.setParentId(MyEntityUtil.getNotNullParentId(null));
        sysFile.setType(SysFileTypeEnum.FILE);
        sysFile.setShowFileName(originalFilename);
        sysFile.setRefFileId(BaseConstant.NEGATIVE_ONE);
        sysFile.setPublicFlag(publicFlag);
        sysFile.setEnableFlag(true);
        sysFile.setDelFlag(false);
        sysFile.setRemark("");

        sysFileService.save(sysFile);

        return sysFile.getId();

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    @Nullable
    public static InputStream privateDownload(long fileId) {

        SysFile sysFile = getPrivateDownloadSysFile(fileId);

        if (SysFileTypeEnum.FOLDER.equals(sysFile.getType())) {
            ApiResultVO.error("操作失败：暂不支持下载文件夹");
        }

        if (BooleanUtil.isFalse(sysFile.getPublicFlag())) { // 如果：不是公开下载

            Long currentUserId = UserUtil.getCurrentUserId();

            // 检查：是否有可读权限
            boolean exists = sysFileAuthService.lambdaQuery().eq(SysFileAuth::getFileId, fileId)
                .eq(SysFileAuth::getUserId, currentUserId).eq(SysFileAuth::getReadFlag, true)
                .eq(BaseEntityNoId::getEnableFlag, true).exists();

            if (BooleanUtil.isFalse(exists)) {
                ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
            }

        }

        if (sysFile.getRefFileId() != BaseConstant.NEGATIVE_ONE) { // 如果有关联的文件，则使用关联文件的信息

            sysFile = getPrivateDownloadSysFile(sysFile.getRefFileId());

        }

        if (SysFileStorageTypeEnum.ALI_YUN.equals(sysFile.getStorageType())) {

            return FileAliYunUtil.download(sysFile.getBucketName(), sysFile.getNewFileName());

        } else if (SysFileStorageTypeEnum.MINIO.equals(sysFile.getStorageType())) {

            return FileMinioUtil.download(sysFile.getBucketName(), sysFile.getNewFileName());

        }

        return null;

    }

    private static SysFile getPrivateDownloadSysFile(long fileId) {

        SysFile sysFile = sysFileService.lambdaQuery()
            .select(SysFile::getBucketName, SysFile::getNewFileName, SysFile::getPublicFlag, SysFile::getRefFileId,
                SysFile::getStorageType, SysFile::getType).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(BaseEntity::getId, fileId).one();

        if (sysFile == null) {
            ApiResultVO.error("操作失败：文件不存在");
        }

        return sysFile;

    }

    /**
     * 批量删除文件：共有和私有
     */
    @SneakyThrows
    public static void removeByFileIdSet(Set<Long> fileIdSet) {

        if (CollUtil.isEmpty(fileIdSet)) {
            return;
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        // 只有：文件拥有者才可以删除
        List<SysFile> sysFileList = sysFileService.lambdaQuery()
            .select(SysFile::getBucketName, SysFile::getNewFileName, SysFile::getStorageType, SysFile::getType)
            .in(BaseEntity::getId, fileIdSet).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysFile::getBelongId, currentUserId).list();

        if (sysFileList.size() != fileIdSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
        }

        boolean anyMatch = sysFileList.stream().anyMatch(it -> SysFileTypeEnum.FOLDER.equals(it.getType()));

        if (anyMatch) {
            ApiResultVO.error("操作失败：暂不支持删除文件夹");
        }

        Set<String> bucketNameSet = sysFileList.stream().map(SysFile::getBucketName).collect(Collectors.toSet());

        if (bucketNameSet.size() != 1) {
            ApiResultVO.error("操作失败：bucketName不相同");
        }

        // 可以随便取一个：bucketName，因为都是一样的
        String bucketName = sysFileList.get(0).getBucketName();

        // 移除：所有文件
        TransactionUtil.exec(() -> {

            sysFileService.removeBatchByIds(fileIdSet);

            sysFileAuthService.lambdaUpdate().in(SysFileAuth::getFileId, fileIdSet).remove();

            Map<SysFileStorageTypeEnum, List<SysFile>> groupMap =
                sysFileList.stream().collect(Collectors.groupingBy(SysFile::getStorageType));

            Set<String> aliYunObjectNameSet = new HashSet<>();
            Set<String> minioObjectNameSet = new HashSet<>();

            for (List<SysFile> item : groupMap.values()) {

                for (SysFile subItem : item) {

                    if (SysFileStorageTypeEnum.ALI_YUN.equals(subItem.getStorageType())) {

                        aliYunObjectNameSet.add(subItem.getNewFileName());

                    } else if (SysFileStorageTypeEnum.MINIO.equals(subItem.getStorageType())) {

                        minioObjectNameSet.add(subItem.getNewFileName());

                    }

                }

            }

            // 移除：文件系统里面的文件
            FileAliYunUtil.remove(bucketName, aliYunObjectNameSet);
            FileMinioUtil.remove(bucketName, minioObjectNameSet);

        });

    }

}
