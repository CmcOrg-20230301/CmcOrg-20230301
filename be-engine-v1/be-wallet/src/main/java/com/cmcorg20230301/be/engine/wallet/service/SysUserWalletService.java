package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletLogTypeEnum;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

public interface SysUserWalletService extends IService<SysUserWalletDO> {

    String frozenByIdSet(NotEmptyIdSet notEmptyIdSet);

    String thawByIdSet(NotEmptyIdSet notEmptyIdSet);

    Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto);

    SysUserWalletDO infoById(NotNullLong notNullLong);

    SysUserWalletDO infoByIdUserSelf();

    String addWithdrawableMoneyBackground(ChangeBigDecimalNumberDTO dto);

    String doAddWithdrawableMoney(Long currentUserId, Date date, Set<Long> idSet, BigDecimal changeNumber,
        SysUserWalletLogTypeEnum sysUserWalletLogTypeEnum, boolean lowErrorFlag, boolean checkWalletEnableFlag,
        boolean tenantFlag);

    // ================================ 分割线

    String changeEnableFlag(NotEmptyIdSet notEmptyIdSet, boolean enableFlag);

    Page<SysUserWalletDO> doMyPage(SysUserWalletPageDTO dto, boolean tenantFlag);

}
