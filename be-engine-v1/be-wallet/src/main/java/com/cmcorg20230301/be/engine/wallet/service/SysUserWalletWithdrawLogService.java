package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndStringValue;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletWithdrawLogPageUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletWithdrawLogDO;

public interface SysUserWalletWithdrawLogService extends IService<SysUserWalletWithdrawLogDO> {

    Page<DictIntegerVO> withdrawStatusDictList();

    Page<SysUserWalletWithdrawLogDO> myPage(SysUserWalletWithdrawLogPageDTO dto);

    Page<SysUserWalletWithdrawLogDO> myPageUserSelf(SysUserWalletWithdrawLogPageUserSelfDTO dto);

    SysUserWalletWithdrawLogDO infoById(NotNullId notNullId);

    SysUserWalletWithdrawLogDO infoByIdUserSelf(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    String deleteByIdSetUserSelf(NotEmptyIdSet notEmptyIdSet);

    String insertOrUpdateUserSelf(SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO dto);

    String commitUserSelf(NotNullId notNullId);

    String revokeUserSelf(NotNullId notNullId);

    String accept(NotNullId notNullId);

    String success(NotNullId notNullId);

    String reject(NotNullIdAndStringValue notNullIdAndStringValue);

}
