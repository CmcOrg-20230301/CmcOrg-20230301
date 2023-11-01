package com.cmcorg20230301.be.engine.other.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppPageDTO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysOtherAppServiceImpl extends ServiceImpl<SysOtherAppMapper, SysOtherAppDO>
    implements SysOtherAppService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysOtherAppInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        // 第三方 appId，不能重复
        boolean exists = lambdaQuery().eq(SysOtherAppDO::getAppId, dto.getAppId())
            .ne(dto.getId() != null, BaseEntity::getId, dto.getId()).eq(BaseEntityNoId::getTenantId, dto.getTenantId())
            .exists();

        if (exists) {
            ApiResultVO.errorMsg("操作失败：第三方 appId不能重复");
        }

        SysOtherAppDO sysOtherAppDO = new SysOtherAppDO();

        sysOtherAppDO.setType(dto.getType());
        sysOtherAppDO.setName(dto.getName());
        sysOtherAppDO.setAppId(dto.getAppId());
        sysOtherAppDO.setSecret(dto.getSecret());

        sysOtherAppDO.setSubscribeReplyContent(MyEntityUtil.getNotNullStr(dto.getSubscribeReplyContent()));
        sysOtherAppDO.setQrCode(MyEntityUtil.getNotNullStr(dto.getQrCode()));

        sysOtherAppDO.setId(dto.getId());
        sysOtherAppDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysOtherAppDO.setDelFlag(false);
        sysOtherAppDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysOtherAppDO);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysOtherAppDO> myPage(SysOtherAppPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysOtherAppDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getAppId()), SysOtherAppDO::getAppId, dto.getAppId())
            .like(StrUtil.isNotBlank(dto.getSubscribeReplyContent()), SysOtherAppDO::getSubscribeReplyContent,
                dto.getSubscribeReplyContent()) //
            .like(StrUtil.isNotBlank(dto.getQrCode()), SysOtherAppDO::getQrCode, dto.getQrCode()) //
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getType() != null, SysOtherAppDO::getType, dto.getType())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .select(BaseEntity::getId, BaseEntityNoIdFather::getTenantId, BaseEntityNoId::getEnableFlag,
                BaseEntityNoId::getRemark, BaseEntityNoIdFather::getCreateId, BaseEntityNoIdFather::getCreateTime,
                BaseEntityNoIdFather::getUpdateId, BaseEntityNoIdFather::getUpdateTime, SysOtherAppDO::getAppId,
                SysOtherAppDO::getName, SysOtherAppDO::getType, SysOtherAppDO::getSubscribeReplyContent,
                SysOtherAppDO::getQrCode).orderByDesc(BaseEntity::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysOtherAppDO infoById(NotNullId notNullId) {

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
     * 通过主键id，获取第三方应用名
     */
    @Override
    public String getNameById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysOtherAppDO sysOtherAppDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .select(SysOtherAppDO::getName).one();

        if (sysOtherAppDO == null) {
            return "";
        }

        return sysOtherAppDO.getName();

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




