package com.cmcorg20230301.be.engine.wallet.service.impl;

import cn.hutool.core.lang.func.Func1;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;
import com.cmcorg20230301.be.engine.wallet.service.SysTenantWalletService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Service
public class SysTenantWalletServiceImpl implements SysTenantWalletService {

    @Resource
    SysUserWalletService sysUserWalletService;

    /**
     * 批量冻结
     */
    @Override
    public String frozenByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 改变：钱包冻结状态
        return sysUserWalletService.changeEnableFlag(notEmptyIdSet, false);

    }

    /**
     * 批量解冻
     */
    @Override
    public String thawByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 改变：钱包冻结状态
        return sysUserWalletService.changeEnableFlag(notEmptyIdSet, true);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto) {

        // 执行
        return sysUserWalletService.doMyPage(dto, true);

    }

    /**
     * 通过租户主键id，查看详情
     */
    @Override
    public SysUserWalletDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return sysUserWalletService.lambdaQuery().eq(SysUserWalletDO::getTenantId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

    }

    /**
     * 通过租户主键 idSet，加减可提现的钱
     */
    @Override
    @DSTransactional
    public String addWithdrawableMoneyBackground(ChangeBigDecimalNumberDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        BigDecimal changeNumber = dto.getNumber();

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        SysUserWalletLogTypeEnum sysUserWalletLogTypeEnum =
            changeNumber.compareTo(BigDecimal.ZERO) > 0 ? SysUserWalletLogTypeEnum.ADD_BACKGROUND :
                SysUserWalletLogTypeEnum.REDUCE_BACKGROUND;

        // 执行
        return sysUserWalletService
            .doAddWithdrawableMoney(currentUserId, new Date(), dto.getIdSet(), changeNumber, sysUserWalletLogTypeEnum,
                false, false, true);

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> sysUserWalletService.lambdaQuery().in(SysUserWalletDO::getTenantId, idSet)
            .eq(SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID).in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

}
