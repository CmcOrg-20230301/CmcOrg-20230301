package com.cmcorg20230301.be.engine.pay.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.pay.base.mapper.SysPayConfigurationMapper;
import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationPageDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayConfigurationService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysPayConfigurationServiceImpl extends ServiceImpl<SysPayConfigurationMapper, SysPayConfigurationDO>
    implements SysPayConfigurationService {

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysPayConfigurationInsertOrUpdateDTO dto) {

        // 每个支付方式，需要单独检查 dto
        dto.getType().getCheckSysPayConfigurationInsertOrUpdateDtoConsumer().accept(dto);

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 如果是默认支付方式，则取消之前的默认支付方式
        if (BooleanUtil.isTrue(dto.getDefaultFlag())) {

            lambdaUpdate().set(SysPayConfigurationDO::getDefaultFlag, false)
                .eq(SysPayConfigurationDO::getDefaultFlag, true).eq(BaseEntityNoId::getTenantId, dto.getTenantId())
                .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).update();

        }

        SysPayConfigurationDO sysPayConfigurationDO = new SysPayConfigurationDO();

        sysPayConfigurationDO.setDefaultFlag(BooleanUtil.isTrue(dto.getDefaultFlag()));

        sysPayConfigurationDO.setType(dto.getType());
        sysPayConfigurationDO.setName(dto.getName());
        sysPayConfigurationDO.setServerUrl(MyEntityUtil.getNotNullStr(dto.getServerUrl()));
        sysPayConfigurationDO.setAppId(dto.getAppId());
        sysPayConfigurationDO.setPrivateKey(dto.getPrivateKey());

        sysPayConfigurationDO.setPlatformPublicKey(MyEntityUtil.getNotNullStr(dto.getPlatformPublicKey()));
        sysPayConfigurationDO.setNotifyUrl(MyEntityUtil.getNotNullStr(dto.getNotifyUrl()));
        sysPayConfigurationDO.setMerchantId(MyEntityUtil.getNotNullStr(dto.getMerchantId()));
        sysPayConfigurationDO.setMerchantSerialNumber(MyEntityUtil.getNotNullStr(dto.getMerchantSerialNumber()));
        sysPayConfigurationDO.setApiV3Key(MyEntityUtil.getNotNullStr(dto.getApiV3Key()));

        sysPayConfigurationDO.setId(dto.getId());
        sysPayConfigurationDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysPayConfigurationDO.setDelFlag(false);
        sysPayConfigurationDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysPayConfigurationDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysPayConfigurationDO> myPage(SysPayConfigurationPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysPayConfigurationDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getAppId()), SysPayConfigurationDO::getAppId, dto.getAppId())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getType() != null, SysPayConfigurationDO::getType, dto.getType())
            .eq(dto.getDefaultFlag() != null, SysPayConfigurationDO::getDefaultFlag, dto.getDefaultFlag())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .select(BaseEntity::getId, BaseEntityNoIdFather::getTenantId, SysPayConfigurationDO::getAppId,
                SysPayConfigurationDO::getType, SysPayConfigurationDO::getName, BaseEntityNoIdFather::getCreateId,
                BaseEntityNoIdFather::getCreateTime, BaseEntityNoIdFather::getUpdateId,
                BaseEntityNoIdFather::getUpdateTime, BaseEntityNoId::getEnableFlag, BaseEntityNoId::getRemark,
                SysPayConfigurationDO::getDefaultFlag).orderByDesc(BaseEntity::getUpdateTime).page(dto.page(true));

    }

    /**
     * 下拉列表
     */
    @Override
    public Page<DictVO> dictList() {

        // 获取：用户关联的租户
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        List<SysPayConfigurationDO> sysPayConfigurationDOList =
            lambdaQuery().select(BaseEntity::getId, SysPayConfigurationDO::getName)
                .in(BaseEntityNoIdFather::getTenantId, tenantIdSet).orderByDesc(BaseEntity::getUpdateTime).list();

        List<DictVO> dictVOList = sysPayConfigurationDOList.stream().map(it -> new DictVO(it.getId(), it.getName()))
            .collect(Collectors.toList());

        return new Page<DictVO>().setTotal(dictVOList.size()).setRecords(dictVOList);

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysPayConfigurationDO infoById(NotNullId notNullId) {

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




