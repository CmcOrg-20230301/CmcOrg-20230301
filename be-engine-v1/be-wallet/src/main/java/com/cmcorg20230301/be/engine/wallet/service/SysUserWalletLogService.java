package com.cmcorg20230301.be.engine.wallet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletLogPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserWalletLogUserSelfPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserWalletLogDO;

public interface SysUserWalletLogService extends IService<SysUserWalletLogDO> {

    Page<SysUserWalletLogDO> myPage(SysUserWalletLogPageDTO dto);

    Page<SysUserWalletLogDO> myPageUserSelf(SysUserWalletLogUserSelfPageDTO dto);

}
