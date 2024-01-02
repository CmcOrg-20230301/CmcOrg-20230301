package com.cmcorg20230301.be.engine.sms.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.sms.base.mapper.SysSmsConfigurationMapper;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationPageDTO;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.service.SysSmsConfigurationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysSmsConfigurationServiceImpl extends ServiceImpl<SysSmsConfigurationMapper, SysSmsConfigurationDO>
    implements SysSmsConfigurationService {

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysSmsConfigurationInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 如果是默认支付方式，则取消之前的默认支付方式
        if (BooleanUtil.isTrue(dto.getDefaultFlag())) {

            lambdaUpdate().set(SysSmsConfigurationDO::getDefaultFlag, false)
                .eq(SysSmsConfigurationDO::getDefaultFlag, true).eq(BaseEntityNoId::getTenantId, dto.getTenantId())
                .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).update();

        }

        SysSmsConfigurationDO sysSmsConfigurationDO = new SysSmsConfigurationDO();

        sysSmsConfigurationDO.setDefaultFlag(BooleanUtil.isTrue(dto.getDefaultFlag()));

        sysSmsConfigurationDO.setType(dto.getType());
        sysSmsConfigurationDO.setName(dto.getName());

        sysSmsConfigurationDO.setSecretId(MyEntityUtil.getNotNullStr(dto.getSecretId()));
        sysSmsConfigurationDO.setSecretKey(MyEntityUtil.getNotNullStr(dto.getSecretKey()));
        sysSmsConfigurationDO.setSdkAppId(MyEntityUtil.getNotNullStr(dto.getSdkAppId()));
        sysSmsConfigurationDO.setSignName(MyEntityUtil.getNotNullStr(dto.getSignName()));
        sysSmsConfigurationDO.setSendDelete(MyEntityUtil.getNotNullStr(dto.getSendDelete()));
        sysSmsConfigurationDO.setSendBind(MyEntityUtil.getNotNullStr(dto.getSendBind()));
        sysSmsConfigurationDO.setSendUpdate(MyEntityUtil.getNotNullStr(dto.getSendUpdate()));
        sysSmsConfigurationDO.setSendUpdatePassword(MyEntityUtil.getNotNullStr(dto.getSendUpdatePassword()));
        sysSmsConfigurationDO.setSendForgetPassword(MyEntityUtil.getNotNullStr(dto.getSendForgetPassword()));
        sysSmsConfigurationDO.setSendSignIn(MyEntityUtil.getNotNullStr(dto.getSendSignIn()));
        sysSmsConfigurationDO.setSendSignUp(MyEntityUtil.getNotNullStr(dto.getSendSignUp()));

        sysSmsConfigurationDO.setSendCommon(MyEntityUtil.getNotNullStr(dto.getSendCommon()));

        sysSmsConfigurationDO.setId(dto.getId());
        sysSmsConfigurationDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysSmsConfigurationDO.setDelFlag(false);
        sysSmsConfigurationDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysSmsConfigurationDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysSmsConfigurationDO> myPage(SysSmsConfigurationPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysSmsConfigurationDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getType() != null, SysSmsConfigurationDO::getType, dto.getType())
            .eq(dto.getDefaultFlag() != null, SysSmsConfigurationDO::getDefaultFlag, dto.getDefaultFlag())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .select(BaseEntity::getId, BaseEntityNoIdSuper::getTenantId, SysSmsConfigurationDO::getType,
                SysSmsConfigurationDO::getName, BaseEntityNoIdSuper::getCreateId, BaseEntityNoIdSuper::getCreateTime,
                BaseEntityNoIdSuper::getUpdateId, BaseEntityNoIdSuper::getUpdateTime, BaseEntityNoId::getEnableFlag,
                BaseEntityNoId::getRemark, SysSmsConfigurationDO::getDefaultFlag).orderByDesc(BaseEntity::getUpdateTime)
            .page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysSmsConfigurationDO infoById(NotNullId notNullId) {

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
