package com.cmcorg20230301.be.engine.file.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.file.base.mapper.SysFileStorageConfigurationMapper;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileStorageConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileStorageConfigurationPageDTO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileStorageConfigurationService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysFileStorageConfigurationServiceImpl
    extends ServiceImpl<SysFileStorageConfigurationMapper, SysFileStorageConfigurationDO>
    implements SysFileStorageConfigurationService {

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysFileStorageConfigurationInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 如果是默认文件存储，则取消之前的默认文件存储
        if (BooleanUtil.isTrue(dto.getDefaultFlag())) {

            lambdaUpdate().set(SysFileStorageConfigurationDO::getDefaultFlag, false)
                .eq(SysFileStorageConfigurationDO::getDefaultFlag, true)
                .eq(BaseEntityNoId::getTenantId, dto.getTenantId())
                .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).update();

        }

        SysFileStorageConfigurationDO sysFileStorageConfigurationDO = new SysFileStorageConfigurationDO();

        sysFileStorageConfigurationDO.setName(dto.getName());
        sysFileStorageConfigurationDO.setType(dto.getType());
        sysFileStorageConfigurationDO.setAccessKey(dto.getAccessKey());
        sysFileStorageConfigurationDO.setSecretKey(dto.getSecretKey());
        sysFileStorageConfigurationDO.setUploadEndpoint(dto.getUploadEndpoint());
        sysFileStorageConfigurationDO.setPublicDownloadEndpoint(dto.getPublicDownloadEndpoint());
        sysFileStorageConfigurationDO.setBucketPublicName(dto.getBucketPublicName());
        sysFileStorageConfigurationDO.setBucketPrivateName(dto.getBucketPrivateName());
        sysFileStorageConfigurationDO.setDefaultFlag(BooleanUtil.isTrue(dto.getDefaultFlag()));

        sysFileStorageConfigurationDO.setId(dto.getId());
        sysFileStorageConfigurationDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysFileStorageConfigurationDO.setDelFlag(false);
        sysFileStorageConfigurationDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysFileStorageConfigurationDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysFileStorageConfigurationDO> myPage(SysFileStorageConfigurationPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery()
            .like(StrUtil.isNotBlank(dto.getName()), SysFileStorageConfigurationDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getAccessKey()), SysFileStorageConfigurationDO::getAccessKey,
                dto.getAccessKey()) //
            .like(StrUtil.isNotBlank(dto.getUploadEndpoint()), SysFileStorageConfigurationDO::getUploadEndpoint,
                dto.getUploadEndpoint()) //
            .like(StrUtil.isNotBlank(dto.getPublicDownloadEndpoint()),
                SysFileStorageConfigurationDO::getPublicDownloadEndpoint, dto.getPublicDownloadEndpoint()) //
            .like(StrUtil.isNotBlank(dto.getBucketPublicName()), SysFileStorageConfigurationDO::getBucketPublicName,
                dto.getBucketPublicName()) //
            .like(StrUtil.isNotBlank(dto.getBucketPrivateName()), SysFileStorageConfigurationDO::getBucketPrivateName,
                dto.getBucketPrivateName()) //
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getType() != null, SysFileStorageConfigurationDO::getType, dto.getType())
            .eq(dto.getDefaultFlag() != null, SysFileStorageConfigurationDO::getDefaultFlag, dto.getDefaultFlag())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .select(BaseEntity::getId, BaseEntityNoIdFather::getTenantId, BaseEntityNoId::getEnableFlag,
                BaseEntityNoId::getRemark, BaseEntityNoIdFather::getCreateId, BaseEntityNoIdFather::getCreateTime,
                BaseEntityNoIdFather::getUpdateId, BaseEntityNoIdFather::getUpdateTime,
                SysFileStorageConfigurationDO::getName, SysFileStorageConfigurationDO::getType,
                SysFileStorageConfigurationDO::getDefaultFlag).orderByDesc(BaseEntity::getUpdateTime)
            .page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysFileStorageConfigurationDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
            .one();

    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        Set<Long> idSet = notEmptyIdSet.getIdSet();

        if (CollUtil.isEmpty(idSet)) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(idSet, getCheckIllegalFunc1(idSet));

        removeByIds(idSet); // 根据 idSet删除

        return BaseBizCodeEnum.OK;

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(BaseEntity::getId, idSet).in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntity> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(BaseEntity::getId, id).select(BaseEntity::getTenantId).one();

    }

}
