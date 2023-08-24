package com.cmcorg20230301.engine.be.file.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.file.base.model.configuration.ISysFile;
import com.cmcorg20230301.engine.be.file.base.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileAuthDO;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.engine.be.file.base.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.engine.be.file.base.model.enums.SysFileTypeEnum;
import com.cmcorg20230301.engine.be.file.base.model.enums.SysFileUploadTypeEnum;
import com.cmcorg20230301.engine.be.file.base.properties.SysFileProperties;
import com.cmcorg20230301.engine.be.file.base.service.SysFileAuthService;
import com.cmcorg20230301.engine.be.file.base.service.SysFileService;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.mysql.util.TransactionUtil;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 文件工具类
 */
@Component
public class SysFileUtil {

    private static SysFileProperties sysFileProperties;

    private static SysFileService sysFileService;
    private static SysFileAuthService sysFileAuthService;

    private static SysUserInfoMapper sysUserInfoMapper;

    private static final Map<Integer, ISysFile> SYS_FILE_MAP = MapUtil.newHashMap();

    public SysFileUtil(SysFileProperties sysFileProperties, SysFileService sysFileService,
        SysFileAuthService sysFileAuthService, SysUserInfoMapper sysUserInfoMapper,
        @Autowired(required = false) List<ISysFile> iSysFileList) {

        SysFileUtil.sysFileProperties = sysFileProperties;

        SysFileUtil.sysFileService = sysFileService;
        SysFileUtil.sysFileAuthService = sysFileAuthService;

        SysFileUtil.sysUserInfoMapper = sysUserInfoMapper;

        if (CollUtil.isNotEmpty(iSysFileList)) {

            for (ISysFile item : iSysFileList) {

                SYS_FILE_MAP.put(item.getSysFileStorageType().getCode(), item);

            }

        }

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
            ApiResultVO.errorMsg("操作失败：上传的文件名不能为空");
        }

        String fileType = dto.getUploadType().checkFileType(dto.getFile());

        if (fileType == null) {

            ApiResultVO.errorMsg("操作失败：暂不支持此文件类型【" + originalFilename + "】，请重新选择");

        }

        return fileType;

    }

    /**
     * 上传文件：公有和私有
     * 备注：objectName 相同的，会被覆盖掉
     *
     * @return 保存到数据库的，文件主键 id
     */
    @SneakyThrows
    @Nullable
    public static Long upload(SysFileUploadDTO dto) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        // 上传文件时的检查
        String fileType = uploadCheckWillError(dto);

        Long sysFileId = null;

        // 如果是：头像
        if (SysFileUploadTypeEnum.AVATAR.equals(dto.getUploadType())) {

            // 通用：上传处理
            sysFileId =
                uploadCommonHandle(dto, fileType, currentUserIdNotAdmin, sysFileProperties.getAvatarStorageType(),

                    (sysFileIdTemp) -> {

                        ChainWrappers.lambdaUpdateChain(sysUserInfoMapper)
                            .eq(SysUserInfoDO::getId, currentUserIdNotAdmin)
                            .set(SysUserInfoDO::getAvatarFileId, sysFileIdTemp).update();

                    });

        }

        return sysFileId;

    }

    /**
     * 通用：上传处理
     */
    @NotNull
    private static Long uploadCommonHandle(SysFileUploadDTO dto, String fileType, Long currentUserId,
        Integer storageType, @Nullable Consumer<Long> consumer) {

        ISysFile iSysFile = SYS_FILE_MAP.get(storageType);

        if (iSysFile == null) {

            ApiResultVO.errorMsg("操作失败：文件存储方式未找到：{}", storageType);

        }

        SysFileStorageTypeEnum sysFileStorageTypeEnum = iSysFile.getSysFileStorageType();

        String folderName = dto.getUploadType().getFolderName();

        String originalFilename = dto.getFile().getOriginalFilename(); // 旧的文件名

        String newFileName = IdUtil.simpleUUID() + "." + fileType; // 新的文件名

        String objectName = folderName + "/" + newFileName;

        String bucketName;

        if (dto.getUploadType().isPublicFlag()) {

            bucketName = iSysFile.getSysFileBaseProperties().getBucketPublicName();

        } else {

            bucketName = iSysFile.getSysFileBaseProperties().getBucketPrivateName();

        }

        if (StrUtil.isBlank(bucketName)) {

            ApiResultVO.errorMsg("操作失败：bucketName为空，请联系管理员");

        }

        // 执行：文件上传
        iSysFile.upload(bucketName, objectName, dto.getFile());

        String finalBucketName = bucketName;

        return TransactionUtil.exec(() -> {

            // 通用保存：文件信息到数据库
            Long sysFileId = saveCommonSysFile(dto, fileType, currentUserId, originalFilename, newFileName, objectName,
                finalBucketName, sysFileStorageTypeEnum);

            if (consumer != null) {

                consumer.accept(sysFileId);

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
        SysFileStorageTypeEnum storageType) {

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
        sysFileDO.setPublicFlag(dto.getUploadType().isPublicFlag());

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
            ApiResultVO.errorMsg("操作失败：暂不支持下载文件夹");
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

        ISysFile iSysFile = SYS_FILE_MAP.get(sysFileDO.getStorageType().getCode());

        if (iSysFile == null) {

            ApiResultVO.errorMsg("操作失败：文件存储位置不存在：{}", sysFileDO.getStorageType().getCode());

        }

        return iSysFile.download(sysFileDO.getBucketName(), sysFileDO.getNewFileName());

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
            ApiResultVO.errorMsg("操作失败：文件不存在");
        }

        return sysFileDO;

    }

    private static LambdaQueryChainWrapper<SysFileDO> getSysFileBaseLambdaQuery() {

        return sysFileService.lambdaQuery()
            .select(SysFileDO::getBucketName, SysFileDO::getNewFileName, SysFileDO::getPublicFlag,
                SysFileDO::getRefFileId, SysFileDO::getStorageType, SysFileDO::getType, BaseEntity::getId,
                SysFileDO::getUri).eq(BaseEntityNoId::getEnableFlag, true);

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

                ISysFile iSysFile = SYS_FILE_MAP.get(item.getStorageType().getCode());

                if (iSysFile != null) {

                    result.put(item.getId(), iSysFile.getUrl(item.getUri()));

                }

            }

        }

        return result;

    }

    /**
     * 批量删除文件：公有和私有
     */
    @SneakyThrows
    public static void removeByFileIdSet(Set<Long> fileIdSet) {

        if (CollUtil.isEmpty(fileIdSet)) {
            return;
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        // 只有：文件拥有者才可以删除
        List<SysFileDO> sysFileDOList = sysFileService.lambdaQuery()
            .select(SysFileDO::getBucketName, SysFileDO::getUri, SysFileDO::getStorageType, SysFileDO::getType)
            .in(BaseEntity::getId, fileIdSet).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysFileDO::getBelongId, currentUserId).list();

        if (sysFileDOList.size() != fileIdSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
        }

        boolean anyMatch = sysFileDOList.stream().anyMatch(it -> SysFileTypeEnum.FOLDER.equals(it.getType()));

        if (anyMatch) {
            ApiResultVO.errorMsg("操作失败：暂不支持删除文件夹");
        }

        Set<String> bucketNameSet = sysFileDOList.stream().map(SysFileDO::getBucketName).collect(Collectors.toSet());

        if (bucketNameSet.size() != 1) {
            ApiResultVO.errorMsg("操作失败：bucketName不相同");
        }

        // 可以随便取一个：bucketName，因为都是一样的
        String bucketName = sysFileDOList.get(0).getBucketName();

        Map<SysFileStorageTypeEnum, List<SysFileDO>> groupMap =
            sysFileDOList.stream().collect(Collectors.groupingBy(SysFileDO::getStorageType));

        for (Map.Entry<SysFileStorageTypeEnum, List<SysFileDO>> item : groupMap.entrySet()) {

            ISysFile iSysFile = SYS_FILE_MAP.get(item.getKey().getCode());

            if (iSysFile == null) {
                continue;
            }

            Set<String> objectNameSet = item.getValue().stream().map(SysFileDO::getUri).collect(Collectors.toSet());

            // 移除：文件存储系统里面的文件
            iSysFile.remove(bucketName, objectNameSet);

        }

        // 移除：所有文件
        TransactionUtil.exec(() -> {

            sysFileService.removeBatchByIds(fileIdSet);

            sysFileAuthService.lambdaUpdate().in(SysFileAuthDO::getFileId, fileIdSet).remove();

        });

    }

}
