package com.cmcorg20230301.engine.be.file.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cmcorg20230301.engine.be.file.aliyun.properties.FileAliYunProperties;
import com.cmcorg20230301.engine.be.file.aliyun.util.FileAliYunUtil;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileAuthDO;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileDO;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件工具类
 */
@Component
public class SysFileUtil {

    private static SysFileProperties sysFileProperties;

    private static SysFileService sysFileService;
    private static SysFileAuthService sysFileAuthService;

    private static FileAliYunProperties fileAliYunProperties;
    private static FileMinioProperties fileMinioProperties;

    public SysFileUtil(SysFileProperties sysFileProperties, SysFileService sysFileService,
        SysFileAuthService sysFileAuthService, FileAliYunProperties fileAliYunProperties,
        FileMinioProperties fileMinioProperties) {

        SysFileUtil.sysFileProperties = sysFileProperties;

        SysFileUtil.sysFileService = sysFileService;
        SysFileUtil.sysFileAuthService = sysFileAuthService;

        SysFileUtil.fileAliYunProperties = fileAliYunProperties;
        SysFileUtil.fileMinioProperties = fileMinioProperties;

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
            sysFileId = uploadCommonHandler(dto, fileType, dto.getUploadType().isPublicFlag());

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

        SysFileDO sysFileDO = new SysFileDO();

        sysFileDO.setBelongId(currentUserId);
        sysFileDO.setBucketName(bucketName);
        sysFileDO.setUri(objectName);
        sysFileDO.setOriginFileName(originalFilename);
        sysFileDO.setNewFileName(newFileName);
        sysFileDO.setFileExtName(fileType);
        sysFileDO.setExtraJson(MyEntityUtil.getNotNullStr(dto.getExtraJson()));
        sysFileDO.setUploadType(dto.getUploadType());
        sysFileDO.setStorageType(storageType);
        sysFileDO.setParentId(MyEntityUtil.getNotNullParentId(null));
        sysFileDO.setType(SysFileTypeEnum.FILE);
        sysFileDO.setShowFileName(originalFilename);
        sysFileDO.setRefFileId(BaseConstant.NEGATIVE_ONE);
        sysFileDO.setPublicFlag(publicFlag);
        sysFileDO.setEnableFlag(true);
        sysFileDO.setDelFlag(false);
        sysFileDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        sysFileService.save(sysFileDO);

        return sysFileDO.getId();

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    @Nullable
    public static InputStream privateDownload(long fileId) {

        SysFileDO sysFileDO = getPrivateDownloadSysFile(fileId);

        if (SysFileTypeEnum.FOLDER.equals(sysFileDO.getType())) {
            ApiResultVO.error("操作失败：暂不支持下载文件夹");
        }

        if (BooleanUtil.isFalse(sysFileDO.getPublicFlag())) { // 如果：不是公开下载

            Long currentUserId = UserUtil.getCurrentUserId();

            // 检查：是否有可读权限
            boolean exists = sysFileAuthService.lambdaQuery().eq(SysFileAuthDO::getFileId, fileId)
                .eq(SysFileAuthDO::getUserId, currentUserId).eq(SysFileAuthDO::getReadFlag, true)
                .eq(BaseEntityNoId::getEnableFlag, true).exists();

            if (BooleanUtil.isFalse(exists)) {
                ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
            }

        }

        // 如果有关联的文件，则使用关联文件的信息，备注：这里是递归获取，要注意层级问题
        sysFileDO = getDeepPrivateDownloadSysFile(sysFileDO);

        if (SysFileStorageTypeEnum.ALI_YUN.equals(sysFileDO.getStorageType())) {

            return FileAliYunUtil.download(sysFileDO.getBucketName(), sysFileDO.getNewFileName());

        } else if (SysFileStorageTypeEnum.MINIO.equals(sysFileDO.getStorageType())) {

            return FileMinioUtil.download(sysFileDO.getBucketName(), sysFileDO.getNewFileName());

        } else {

            ApiResultVO.error("操作失败：文件存储位置不存在");
            return null; // 备注：这里不会执行，只是为了通过语法检查

        }

    }

    /**
     * 如果有关联的文件，则使用关联文件的信息，备注：这里是递归获取，要注意层级问题
     */
    @NotNull
    private static SysFileDO getDeepPrivateDownloadSysFile(SysFileDO sysFileDO) {

        if (sysFileDO.getRefFileId() != BaseConstant.NEGATIVE_ONE) {

            sysFileDO = getPrivateDownloadSysFile(sysFileDO.getRefFileId());

            return getDeepPrivateDownloadSysFile(sysFileDO);

        }

        return sysFileDO;

    }

    @NotNull
    private static SysFileDO getPrivateDownloadSysFile(long fileId) {

        SysFileDO sysFileDO = getSysFileBaseLambdaQuery().eq(BaseEntity::getId, fileId).one();

        if (sysFileDO == null) {
            ApiResultVO.error("操作失败：文件不存在");
        }

        return sysFileDO;

    }

    private static LambdaQueryChainWrapper<SysFileDO> getSysFileBaseLambdaQuery() {

        return sysFileService.lambdaQuery()
            .select(SysFileDO::getBucketName, SysFileDO::getNewFileName, SysFileDO::getPublicFlag,
                SysFileDO::getRefFileId, SysFileDO::getStorageType, SysFileDO::getType, BaseEntity::getId)
            .eq(BaseEntityNoId::getEnableFlag, true);

    }

    /**
     * 获取：公开文件的 url
     */
    @NotNull
    public static Map<Long, String> getPublicUrl(Set<Long> fileIdSet) {

        List<SysFileDO> sysFileDOList = getSysFileBaseLambdaQuery().in(BaseEntity::getId, fileIdSet).list();

        Map<Long, String> result = new HashMap<>(sysFileDOList.size());

        for (SysFileDO item : sysFileDOList) {

            if (BooleanUtil.isTrue(item.getPublicFlag())) { // 如果：是公开下载

                String url = null;

                if (SysFileStorageTypeEnum.ALI_YUN.equals(item.getStorageType())) {

                    url = fileAliYunProperties.getPublicDownloadEndpoint() + "/" + fileAliYunProperties
                        .getBucketPublicName() + "/" + item.getUri();

                } else if (SysFileStorageTypeEnum.MINIO.equals(item.getStorageType())) {

                    url = fileMinioProperties.getPublicDownloadEndpoint() + "/" + fileMinioProperties
                        .getBucketPublicName() + "/" + item.getUri();

                }

                if (url != null) {

                    result.put(item.getId(), url);

                }

            }

        }

        return result;

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
        List<SysFileDO> sysFileDOList = sysFileService.lambdaQuery()
            .select(SysFileDO::getBucketName, SysFileDO::getNewFileName, SysFileDO::getStorageType, SysFileDO::getType)
            .in(BaseEntity::getId, fileIdSet).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysFileDO::getBelongId, currentUserId).list();

        if (sysFileDOList.size() != fileIdSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
        }

        boolean anyMatch = sysFileDOList.stream().anyMatch(it -> SysFileTypeEnum.FOLDER.equals(it.getType()));

        if (anyMatch) {
            ApiResultVO.error("操作失败：暂不支持删除文件夹");
        }

        Set<String> bucketNameSet = sysFileDOList.stream().map(SysFileDO::getBucketName).collect(Collectors.toSet());

        if (bucketNameSet.size() != 1) {
            ApiResultVO.error("操作失败：bucketName不相同");
        }

        // 可以随便取一个：bucketName，因为都是一样的
        String bucketName = sysFileDOList.get(0).getBucketName();

        // 移除：所有文件
        TransactionUtil.exec(() -> {

            sysFileService.removeBatchByIds(fileIdSet);

            sysFileAuthService.lambdaUpdate().in(SysFileAuthDO::getFileId, fileIdSet).remove();

            Map<SysFileStorageTypeEnum, List<SysFileDO>> groupMap =
                sysFileDOList.stream().collect(Collectors.groupingBy(SysFileDO::getStorageType));

            Set<String> aliYunObjectNameSet = new HashSet<>();
            Set<String> minioObjectNameSet = new HashSet<>();

            for (List<SysFileDO> item : groupMap.values()) {

                for (SysFileDO subItem : item) {

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
