package com.cmcorg20230301.be.engine.file.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.datasource.util.TransactionUtil;
import com.cmcorg20230301.be.engine.file.base.mapper.SysFileStorageConfigurationMapper;
import com.cmcorg20230301.be.engine.file.base.model.bo.SysFileUploadBO;
import com.cmcorg20230301.be.engine.file.base.model.configuration.ISysFileRemove;
import com.cmcorg20230301.be.engine.file.base.model.configuration.ISysFileStorage;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileAuthDO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileTypeEnum;
import com.cmcorg20230301.be.engine.file.base.service.SysFileAuthService;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.enums.SysFileUploadTypeEnum;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysFileStorageType;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;
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

    private static SysFileService sysFileService;
    private static SysFileAuthService sysFileAuthService;

    private static SysUserInfoMapper sysUserInfoMapper;

    private static final Map<Integer, ISysFileStorage> SYS_FILE_STORAGE_MAP = MapUtil.newHashMap();

    private static List<ISysFileRemove> iSysFileRemoveList;

    private static SysFileStorageConfigurationMapper sysFileStorageConfigurationMapper;

    public SysFileUtil(SysFileService sysFileService, SysFileAuthService sysFileAuthService,
        SysUserInfoMapper sysUserInfoMapper,
        @Autowired(required = false) @Nullable List<ISysFileStorage> iSysFileStorageList,
        @Autowired(required = false) @Nullable List<ISysFileRemove> iSysFileRemoveList,
        SysFileStorageConfigurationMapper sysFileStorageConfigurationMapper) {

        SysFileUtil.sysFileService = sysFileService;
        SysFileUtil.sysFileAuthService = sysFileAuthService;

        SysFileUtil.sysUserInfoMapper = sysUserInfoMapper;

        if (CollUtil.isNotEmpty(iSysFileStorageList)) {

            for (ISysFileStorage item : iSysFileStorageList) {

                SYS_FILE_STORAGE_MAP.put(item.getSysFileStorageType().getCode(), item);

            }

        }

        SysFileUtil.iSysFileRemoveList = iSysFileRemoveList;
        SysFileUtil.sysFileStorageConfigurationMapper = sysFileStorageConfigurationMapper;

    }

    /**
     * 上传文件：公有和私有
     * 备注：objectName 相同的，会被覆盖掉
     *
     * @return 保存到数据库的，文件主键 id
     */
    @SneakyThrows
    @Nullable
    public static Long upload(SysFileUploadBO bo) {

        // 上传文件时的检查
        String fileType = SysFileUploadTypeEnum.uploadCheckWillError(bo.getFile(), bo.getUploadType());

        Long resultSysFileId = null;

        // 如果是：头像
        if (SysFileUploadTypeEnum.AVATAR.equals(bo.getUploadType())) {

            // 通用：上传处理
            resultSysFileId = uploadCommonHandle(bo, fileType, null,

                (sysFileId) -> {

                    ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, bo.getUserId())
                        .set(SysUserInfoDO::getAvatarFileId, sysFileId).update();

                }, null);

        }

        return resultSysFileId;

    }

    /**
     * 通用：上传处理
     */
    @NotNull
    public static Long uploadCommonHandle(SysFileUploadBO bo, String fileType,
        @Nullable ISysFileStorageType iSysFileStorageType, @Nullable Consumer<Long> consumer,
        @Nullable SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        // 获取：存储方式的配置
        sysFileStorageConfigurationDO =
            getSysFileStorageConfigurationDO(bo.getTenantId(), iSysFileStorageType, sysFileStorageConfigurationDO);

        Integer storageType = sysFileStorageConfigurationDO.getType();

        ISysFileStorage iSysFileStorage = SYS_FILE_STORAGE_MAP.get(storageType);

        if (iSysFileStorage == null) {

            ApiResultVO.error("操作失败：文件存储方式未找到", storageType);

        }

        String folderName = bo.getUploadType().getFolderName();

        String originalFilename = bo.getFile().getOriginalFilename(); // 旧的文件名

        String newFileName = IdUtil.simpleUUID() + "." + fileType; // 新的文件名

        String objectName = folderName + "/" + newFileName;

        String bucketName;

        if (bo.getUploadType().isPublicFlag()) {

            bucketName = sysFileStorageConfigurationDO.getBucketPublicName();

        } else {

            bucketName = sysFileStorageConfigurationDO.getBucketPrivateName();

        }

        if (StrUtil.isBlank(bucketName)) {

            ApiResultVO.errorMsg("操作失败：bucketName为空，请联系管理员");

        }

        // 执行：文件上传
        iSysFileStorage.upload(bucketName, objectName, bo.getFile(), sysFileStorageConfigurationDO);

        String finalBucketName = bucketName;

        SysFileStorageConfigurationDO finalSysFileStorageConfigurationDO = sysFileStorageConfigurationDO;

        return TransactionUtil.exec(() -> {

            // 通用保存：文件信息到数据库
            Long sysFileId = saveCommonSysFile(bo, fileType, originalFilename, newFileName, objectName, finalBucketName,
                finalSysFileStorageConfigurationDO);

            if (consumer != null) {

                consumer.accept(sysFileId);

            }

            return sysFileId;

        });

    }

    /**
     * 获取：存储方式的配置
     */
    @NotNull
    private static SysFileStorageConfigurationDO getSysFileStorageConfigurationDO(Long tenantId,
        @Nullable ISysFileStorageType iSysFileStorageType,
        @Nullable SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        if (sysFileStorageConfigurationDO != null) {

            return sysFileStorageConfigurationDO;

        }

        // 执行获取
        sysFileStorageConfigurationDO = execGetFileStorageConfigurationDO(tenantId, iSysFileStorageType);

        if (sysFileStorageConfigurationDO == null) {

            ApiResultVO.errorMsg("操作失败：暂未配置文件存储方式，请联系管理员");

        }

        return sysFileStorageConfigurationDO;

    }

    /**
     * 执行获取
     */
    private static SysFileStorageConfigurationDO execGetFileStorageConfigurationDO(Long tenantId,
        @Nullable ISysFileStorageType iSysFileStorageType) {

        SysFileStorageConfigurationDO sysFileStorageConfigurationDO = null;

        if (iSysFileStorageType == null) {

            // 获取：默认的存储方式
            sysFileStorageConfigurationDO = ChainWrappers.lambdaQueryChain(sysFileStorageConfigurationMapper)
                .eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(BaseEntityNoId::getEnableFlag, true)
                .eq(SysFileStorageConfigurationDO::getDefaultFlag, true).one();

        } else {

            // 根据传入的类型，选择一个存储方式
            List<SysFileStorageConfigurationDO> sysFileStorageConfigurationDOList =
                ChainWrappers.lambdaQueryChain(sysFileStorageConfigurationMapper)
                    .eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(BaseEntityNoId::getEnableFlag, true)
                    .eq(SysFileStorageConfigurationDO::getType, iSysFileStorageType.getCode()).list();

            if (CollUtil.isNotEmpty(sysFileStorageConfigurationDOList)) {

                // 随机选择一个存储方式
                sysFileStorageConfigurationDO = RandomUtil.randomEle(sysFileStorageConfigurationDOList);

            }

        }

        if (sysFileStorageConfigurationDO == null && !BaseConstant.TOP_TENANT_ID.equals(tenantId)) {

            SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(tenantId);

            if (sysTenantDO != null) {

                Long parentTenantId = sysTenantDO.getParentId();

                // 获取：上一级租户的文件存储配置
                return execGetFileStorageConfigurationDO(parentTenantId, iSysFileStorageType);

            }

        }

        return sysFileStorageConfigurationDO;

    }

    /**
     * 通用保存：文件信息到数据库
     */
    @NotNull
    public static Long saveCommonSysFile(SysFileUploadBO bo, String fileType, String originalFilename,
        String newFileName, String objectName, String bucketName,
        SysFileStorageConfigurationDO sysFileStorageConfigurationDO) {

        SysFileDO sysFileDO = new SysFileDO();

        sysFileDO.setTenantId(bo.getTenantId());

        sysFileDO.setBelongId(bo.getUserId());

        sysFileDO.setBucketName(bucketName);

        sysFileDO.setUri(objectName);

        sysFileDO.setOriginFileName(MyEntityUtil.getNotNullStr(originalFilename));

        sysFileDO.setNewFileName(newFileName);

        sysFileDO.setFileExtName(fileType);

        sysFileDO.setExtraJson(MyEntityUtil.getNotNullStr(bo.getExtraJson()));

        sysFileDO.setUploadType(bo.getUploadType().getCode());

        sysFileDO.setStorageConfigurationId(sysFileStorageConfigurationDO.getId());

        sysFileDO.setStorageType(sysFileStorageConfigurationDO.getType());

        sysFileDO.setParentId(MyEntityUtil.getNotNullParentId(null));

        sysFileDO.setType(SysFileTypeEnum.FILE);

        sysFileDO.setShowFileName(MyEntityUtil.getNotNullStr(originalFilename));

        sysFileDO.setRefFileId(BaseConstant.NEGATIVE_ONE);

        sysFileDO.setPublicFlag(bo.getUploadType().isPublicFlag());

        sysFileDO.setFileSize(bo.getFile().getSize());

        sysFileDO.setRefId(MyEntityUtil.getNotNullLong(bo.getRefId()));

        sysFileDO.setEnableFlag(true);

        sysFileDO.setDelFlag(false);

        sysFileDO.setRemark(MyEntityUtil.getNotNullStr(bo.getRemark()));

        sysFileService.save(sysFileDO);

        return sysFileDO.getId();

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    @Nullable
    public static InputStream privateDownload(long fileId, @Nullable CallBack<String> fileNameCallBack) {

        Set<Long> idSet = CollUtil.newHashSet(fileId);

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, tenantIdSet -> sysFileService.lambdaQuery().in(BaseEntity::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        SysFileDO sysFileDO = getPrivateDownloadSysFile(fileId);

        if (SysFileTypeEnum.FOLDER.equals(sysFileDO.getType())) {
            ApiResultVO.errorMsg("操作失败：暂不支持下载文件夹");
        }

        if (BooleanUtil.isFalse(sysFileDO.getPublicFlag())) { // 如果：不是公开下载

            Long currentUserId = UserUtil.getCurrentUserId();

            // 检查：是否是该文件的拥有者
            if (!currentUserId.equals(sysFileDO.getBelongId())) {

                // 检查：是否有可读权限
                boolean exists = sysFileAuthService.lambdaQuery().eq(SysFileAuthDO::getFileId, fileId)
                    .eq(SysFileAuthDO::getUserId, currentUserId).eq(SysFileAuthDO::getReadFlag, true)
                    .eq(BaseEntityNoId::getEnableFlag, true).exists();

                if (BooleanUtil.isFalse(exists)) {
                    ApiResultVO.error(BaseBizCodeEnum.INSUFFICIENT_PERMISSIONS);
                }

            }

        }

        // 如果有关联的文件，则使用关联文件的信息，备注：这里是递归获取，要注意层级问题
        sysFileDO = getDeepPrivateDownloadSysFile(sysFileDO);

        ISysFileStorage iSysFileStorage = SYS_FILE_STORAGE_MAP.get(sysFileDO.getStorageType());

        if (iSysFileStorage == null) {

            ApiResultVO.error("操作失败：文件存储位置不存在", sysFileDO.getStorageType());

        }

        Long storageConfigurationId = sysFileDO.getStorageConfigurationId();

        SysFileStorageConfigurationDO sysFileStorageConfigurationDO =
            ChainWrappers.lambdaQueryChain(sysFileStorageConfigurationMapper)
                .eq(BaseEntity::getId, storageConfigurationId).eq(BaseEntityNoId::getEnableFlag, true).one();

        if (sysFileStorageConfigurationDO == null) {

            ApiResultVO.error("操作失败：文件存储配置不存在", storageConfigurationId);

        }

        if (fileNameCallBack != null) {

            fileNameCallBack.setValue(sysFileDO.getOriginFileName());

        }

        return iSysFileStorage.download(sysFileDO.getBucketName(), sysFileDO.getUri(), sysFileStorageConfigurationDO);

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
                SysFileDO::getUri, SysFileDO::getStorageConfigurationId, SysFileDO::getBelongId,
                SysFileDO::getOriginFileName).eq(BaseEntityNoId::getEnableFlag, true);

    }

    /**
     * 获取：公开文件的 url
     */
    @NotNull
    public static Map<Long, String> getPublicUrl(Set<Long> fileIdSet) {

        // 先移除：所有 -1的文件 id
        fileIdSet.removeAll(CollUtil.newHashSet(-1L));

        if (CollUtil.isEmpty(fileIdSet)) {
            return MapUtil.newHashMap();
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(fileIdSet,
            tenantIdSet -> sysFileService.lambdaQuery().in(BaseEntity::getId, fileIdSet)
                .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        List<SysFileDO> sysFileDOList =
            getSysFileBaseLambdaQuery().in(BaseEntity::getId, fileIdSet).eq(SysFileDO::getPublicFlag, true).list();

        Map<Long, String> result = new HashMap<>(sysFileDOList.size());

        Set<Long> sysFileStorageConfigurationIdSet =
            sysFileDOList.stream().map(SysFileDO::getStorageConfigurationId).collect(Collectors.toSet());

        if (CollUtil.isEmpty(sysFileStorageConfigurationIdSet)) {
            return MapUtil.newHashMap();
        }

        List<SysFileStorageConfigurationDO> sysFileStorageConfigurationDOList =
            ChainWrappers.lambdaQueryChain(sysFileStorageConfigurationMapper)
                .in(BaseEntity::getId, sysFileStorageConfigurationIdSet).eq(BaseEntityNoId::getEnableFlag, true).list();

        if (CollUtil.isEmpty(sysFileStorageConfigurationDOList)) {
            return MapUtil.newHashMap();
        }

        // 通过：id进行分组
        Map<Long, SysFileStorageConfigurationDO> sysFileStorageConfigurationIdMap =
            sysFileStorageConfigurationDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> it));

        for (SysFileDO item : sysFileDOList) {

            SysFileStorageConfigurationDO sysFileStorageConfigurationDO =
                sysFileStorageConfigurationIdMap.get(item.getStorageConfigurationId());

            if (sysFileStorageConfigurationDO == null) {
                continue;
            }

            ISysFileStorage iSysFileStorage = SYS_FILE_STORAGE_MAP.get(sysFileStorageConfigurationDO.getType());

            if (iSysFileStorage != null) {

                result.put(item.getId(),
                    iSysFileStorage.getUrl(item.getUri(), item.getBucketName(), sysFileStorageConfigurationDO));

            }

        }

        return result;

    }

    /**
     * 批量删除文件：公有和私有
     *
     * @param checkBelongFlag 是否检查：文件拥有者才可以删除
     */
    @SneakyThrows
    public static void removeByFileIdSet(Set<Long> fileIdSet, boolean checkBelongFlag) {

        if (CollUtil.isEmpty(fileIdSet)) {
            return;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(fileIdSet,
            tenantIdSet -> sysFileService.lambdaQuery().in(BaseEntity::getId, fileIdSet)
                .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        List<SysFileDO> sysFileDOList;

        LambdaQueryChainWrapper<SysFileDO> lambdaQueryChainWrapper = sysFileService.lambdaQuery()
            .select(SysFileDO::getBucketName, SysFileDO::getUri, SysFileDO::getType, BaseEntity::getId,
                SysFileDO::getStorageConfigurationId).in(BaseEntity::getId, fileIdSet);

        if (checkBelongFlag) {

            Long currentUserId = UserUtil.getCurrentUserId();

            // 只有：文件拥有者才可以删除
            sysFileDOList = lambdaQueryChainWrapper.eq(SysFileDO::getBelongId, currentUserId).list();

        } else {

            // 直接删除
            sysFileDOList = lambdaQueryChainWrapper.list();

        }

        if (sysFileDOList.size() == 0) {
            ApiResultVO.errorMsg("操作失败：文件拥有者才可以删除");
        }

        boolean anyMatch = sysFileDOList.stream().anyMatch(it -> SysFileTypeEnum.FOLDER.equals(it.getType()));

        if (anyMatch) {
            ApiResultVO.errorMsg("操作失败：暂不支持删除文件夹");
        }

        // 移除：文件存储服务器里面的文件
        removeSysFileStorage(sysFileDOList);

        Set<Long> finalFileIdSet = sysFileDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        // 移除：所有文件
        TransactionUtil.exec(() -> {

            sysFileService.removeBatchByIds(finalFileIdSet);

            sysFileAuthService.lambdaUpdate().in(SysFileAuthDO::getFileId, finalFileIdSet).remove();

            if (CollUtil.isNotEmpty(iSysFileRemoveList) && CollUtil.isNotEmpty(finalFileIdSet)) {

                for (ISysFileRemove item : iSysFileRemoveList) {

                    item.handle(finalFileIdSet); // 额外处理：当文件进行移除时

                }

            }

        });

    }

    /**
     * 移除：文件存储服务器里面的文件
     */
    private static void removeSysFileStorage(List<SysFileDO> sysFileDOList) {

        if (CollUtil.isEmpty(sysFileDOList)) {
            return;
        }

        // 根据：存储类型 id分类
        Map<Long, List<SysFileDO>> storageTypeGroupMap =
            sysFileDOList.stream().collect(Collectors.groupingBy(SysFileDO::getStorageConfigurationId));

        List<SysFileStorageConfigurationDO> sysFileStorageConfigurationDOList =
            ChainWrappers.lambdaQueryChain(sysFileStorageConfigurationMapper)
                .in(BaseEntity::getId, storageTypeGroupMap.keySet()).list();

        if (CollUtil.isEmpty(sysFileStorageConfigurationDOList)) {
            return;
        }

        // 通过：id进行分组
        Map<Long, SysFileStorageConfigurationDO> sysFileStorageConfigurationIdMap =
            sysFileStorageConfigurationDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> it));

        for (Map.Entry<Long, List<SysFileDO>> item : storageTypeGroupMap.entrySet()) {

            SysFileStorageConfigurationDO sysFileStorageConfigurationDO =
                sysFileStorageConfigurationIdMap.get(item.getKey());

            if (sysFileStorageConfigurationDO == null) {
                continue;
            }

            ISysFileStorage iSysFileStorage = SYS_FILE_STORAGE_MAP.get(sysFileStorageConfigurationDO.getType());

            if (iSysFileStorage == null) {
                continue;
            }

            // 根据：桶名，进行分类
            Map<String, Set<String>> bucketGroupMap = item.getValue().stream().collect(Collectors
                .groupingBy(SysFileDO::getBucketName, Collectors.mapping(SysFileDO::getUri, Collectors.toSet())));

            for (Map.Entry<String, Set<String>> subItem : bucketGroupMap.entrySet()) {

                // 移除：文件存储系统里面的文件
                iSysFileStorage.remove(subItem.getKey(), subItem.getValue(), sysFileStorageConfigurationDO);

            }

        }

    }

}
