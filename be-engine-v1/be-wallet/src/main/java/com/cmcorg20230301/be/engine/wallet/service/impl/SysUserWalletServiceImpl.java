package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletMapper;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysUserWalletServiceImpl extends ServiceImpl<SysUserWalletMapper, SysUserWalletDO>
    implements SysUserWalletService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysUserWalletInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        SysUserWalletDO sysUserWalletDO = new SysUserWalletDO();

        sysUserWalletDO.setId(dto.getId());
        sysUserWalletDO.setTotalMoney(dto.getTotalMoney());
        sysUserWalletDO.setWithdrawableMoney(dto.getWithdrawableMoney());
        sysUserWalletDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        sysUserWalletDO.setDelFlag(false);
        sysUserWalletDO.setRemark("");

        saveOrUpdate(sysUserWalletDO); // 操作数据库

        return BaseBizCodeEnum.OK;

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery().eq(dto.getId() != null, SysUserWalletDO::getId, dto.getId())
            .eq(dto.getEnableFlag() != null, BaseEntityNoId::getEnableFlag, dto.getEnableFlag())
            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
            .orderByDesc(SysUserWalletDO::getUpdateTime).page(dto.page(true));

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserWalletDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return lambdaQuery().eq(SysUserWalletDO::getId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

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

        return tenantIdSet -> lambdaQuery().in(SysUserWalletDO::getId, idSet)
            .in(BaseEntityNoId::getTenantId, tenantIdSet).count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntityNoIdFather> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(SysUserWalletDO::getId, id).select(SysUserWalletDO::getTenantId).one();

    }

}
