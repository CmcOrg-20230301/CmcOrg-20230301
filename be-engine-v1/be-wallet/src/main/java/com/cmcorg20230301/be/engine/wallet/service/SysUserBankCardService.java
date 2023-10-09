package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;

public interface SysUserBankCardService extends IService<SysUserBankCardDO> {

    String insertOrUpdateUserSelf(SysUserBankCardInsertOrUpdateUserSelfDTO dto);

    Page<SysUserBankCardDO> myPage(SysUserBankCardPageDTO dto);

    SysUserBankCardDO infoById(NotNullId notNullId);

    SysUserBankCardDO infoByIdUserSelf();

}
