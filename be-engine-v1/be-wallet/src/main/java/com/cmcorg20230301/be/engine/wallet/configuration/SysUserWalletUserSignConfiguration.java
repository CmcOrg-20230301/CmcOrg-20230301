package com.cmcorg20230301.be.engine.wallet.configuration;

import com.cmcorg20230301.be.engine.security.model.configuration.IUserSignConfiguration;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.service.SysUserWalletService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Set;

@Component
public class SysUserWalletUserSignConfiguration implements IUserSignConfiguration {

    @Resource
    SysUserWalletService sysUserWalletService;

    @Override
    public void signUp(@NotNull Long userId, @NotNull Long tenantId) {

        SysUserWalletDO sysUserWalletDO = new SysUserWalletDO();

        sysUserWalletDO.setId(userId);

        sysUserWalletDO.setTotalMoney(BigDecimal.ZERO);
        sysUserWalletDO.setWithdrawableMoney(BigDecimal.ZERO);

        sysUserWalletDO.setEnableFlag(true);
        sysUserWalletDO.setDelFlag(false);
        sysUserWalletDO.setRemark("");
        sysUserWalletDO.setTenantId(tenantId);

        sysUserWalletService.save(sysUserWalletDO);

    }

    @Override
    public void delete(Set<Long> userIdSet) {

        // nothing

    }

}
