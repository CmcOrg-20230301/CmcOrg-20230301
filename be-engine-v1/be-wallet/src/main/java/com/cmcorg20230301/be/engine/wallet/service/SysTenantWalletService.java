package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeBigDecimalNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;

import java.util.List;

public interface SysTenantWalletService {

    String frozenByIdSet(NotEmptyIdSet notEmptyIdSet);

    String thawByIdSet(NotEmptyIdSet notEmptyIdSet);

    Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto);

    List<SysUserWalletDO> tree(SysUserWalletPageDTO dto);

    SysUserWalletDO infoById(NotNullLong notNullLong);

    String addWithdrawableMoneyBackground(ChangeBigDecimalNumberDTO dto);

}
