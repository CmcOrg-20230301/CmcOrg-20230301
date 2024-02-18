package com.cmcorg20230301.be.engine.api.token.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.api.token.mapper.SysApiTokenMapper;
import com.cmcorg20230301.be.engine.api.token.model.dto.SysApiTokenInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.api.token.model.dto.SysApiTokenPageDTO;
import com.cmcorg20230301.be.engine.api.token.model.entity.SysApiTokenDO;
import com.cmcorg20230301.be.engine.api.token.service.SysApiTokenService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysApiTokenServiceImpl extends ServiceImpl<SysApiTokenMapper, SysApiTokenDO>
        implements SysApiTokenService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysApiTokenInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
                getTenantIdBaseEntityFunc1());

        SysApiTokenDO sysApiTokenDO = new SysApiTokenDO();

        sysApiTokenDO.setId(dto.getId());
        sysApiTokenDO.setUserId(UserUtil.getCurrentUserId());
        sysApiTokenDO.setTenantId(UserUtil.getCurrentTenantIdDefault());

        if (dto.getId() == null) {

            sysApiTokenDO.setToken(IdUtil.simpleUUID());

        }

        sysApiTokenDO.setName(dto.getName());

        saveOrUpdate(sysApiTokenDO);

        return sysApiTokenDO.getToken();

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysApiTokenDO> myPage(SysApiTokenPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysApiTokenDO::getName, dto.getName())
                .in(SysApiTokenDO::getTenantId, dto.getTenantIdSet()) //
                .page(dto.updateTimeDescDefaultOrderPage(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysApiTokenDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(SysApiTokenDO::getId, notNullId.getId()).in(SysApiTokenDO::getTenantId, queryTenantIdSet)
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

        removeByIds(idSet);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(SysApiTokenDO::getId, idSet).in(SysApiTokenDO::getTenantId, tenantIdSet)
                .count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntityNoIdSuper> getTenantIdBaseEntityFunc1() {

        return id -> {

            SysApiTokenDO sysApiTokenDO = lambdaQuery().eq(SysApiTokenDO::getId, id).select(SysApiTokenDO::getTenantId).one();

            if (sysApiTokenDO == null) {
                return null;
            }

            BaseEntityNoIdSuper baseEntityNoIdSuper = new BaseEntityNoIdSuper();

            baseEntityNoIdSuper.setTenantId(sysApiTokenDO.getTenantId());

            return baseEntityNoIdSuper;

        };

    }

}
