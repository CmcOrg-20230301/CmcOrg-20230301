package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullLong;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;

import java.util.List;

public interface SysTenantBankCardService {

    String insertOrUpdateTenant(SysUserBankCardInsertOrUpdateUserSelfDTO dto);

    Page<SysUserBankCardDO> myPage(SysUserBankCardPageDTO dto);

    List<SysUserBankCardDO> tree(SysUserBankCardPageDTO dto);

    SysUserBankCardDO infoById(NotNullLong notNullLong);

}
