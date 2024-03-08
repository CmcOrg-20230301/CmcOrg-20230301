package com.cmcorg20230301.be.engine.wallet.configuration;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.security.model.configuration.IUserSignConfiguration;
import com.cmcorg20230301.be.engine.wallet.mapper.SysUserWalletMapper;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;

@Component
public class SysUserWalletUserSignConfiguration implements IUserSignConfiguration {

    @Resource
    SysUserWalletMapper sysUserWalletMapper;

    @Override
    public Object signUp(@NotNull Long userId, @NotNull Long tenantId) {

        SysUserWalletDO sysUserWalletDO = getInitSysUserWalletDO(userId, tenantId);

        sysUserWalletMapper.insert(sysUserWalletDO);

        return sysUserWalletDO;

    }

    /**
     * 获取：一个初始的 SysUserWalletDO对象
     */
    @NotNull
    public static SysUserWalletDO getInitSysUserWalletDO(@NotNull Long userId, @NotNull Long tenantId) {

        SysUserWalletDO sysUserWalletDO = new SysUserWalletDO();

        sysUserWalletDO.setId(userId);

        sysUserWalletDO.setWithdrawableMoney(BigDecimal.ZERO);
        sysUserWalletDO.setWithdrawablePreUseMoney(BigDecimal.ZERO);

        sysUserWalletDO.setEnableFlag(true);
        sysUserWalletDO.setDelFlag(false);
        sysUserWalletDO.setRemark("");
        sysUserWalletDO.setTenantId(tenantId);

        return sysUserWalletDO;

    }

    @Override
    public void delete(Set<Long> userIdSet) {

        ChainWrappers.lambdaUpdateChain(sysUserWalletMapper).in(SysUserWalletDO::getId, userIdSet).remove();

    }

}
