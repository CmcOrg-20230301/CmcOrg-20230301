package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletDO;

public interface SysUserWalletService extends IService<SysUserWalletDO> {

    String insertOrUpdate(SysUserWalletInsertOrUpdateDTO dto);

    Page<SysUserWalletDO> myPage(SysUserWalletPageDTO dto);

    SysUserWalletDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
