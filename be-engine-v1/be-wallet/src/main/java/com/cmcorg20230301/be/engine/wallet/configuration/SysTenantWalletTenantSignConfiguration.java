package com.cmcorg20230301.be.engine.wallet.configuration;

import java.util.Set;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletMapper;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;

@Component
public class SysTenantWalletTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysUserWalletMapper sysUserWalletMapper;

    @Override
    public void signUp(@NotNull Long tenantId) {

        SysUserWalletDO sysUserWalletDO =
            SysUserWalletUserSignConfiguration.getInitSysUserWalletDO(BaseConstant.TENANT_USER_ID, tenantId);

        sysUserWalletMapper.insert(sysUserWalletDO);

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        ChainWrappers.lambdaUpdateChain(sysUserWalletMapper).eq(SysUserWalletDO::getId, BaseConstant.TENANT_USER_ID)
            .in(SysUserWalletDO::getTenantId, tenantIdSet).remove();

    }

}
