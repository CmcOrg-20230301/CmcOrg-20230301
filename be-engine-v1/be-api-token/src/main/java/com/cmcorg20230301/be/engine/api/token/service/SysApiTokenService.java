package com.cmcorg20230301.be.engine.api.token.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.api.token.model.dto.SysApiTokenInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.api.token.model.dto.SysApiTokenPageDTO;
import com.cmcorg20230301.be.engine.api.token.model.entity.SysApiTokenDO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;

public interface SysApiTokenService extends IService<SysApiTokenDO> {

    String insertOrUpdate(SysApiTokenInsertOrUpdateDTO dto);

    Page<SysApiTokenDO> myPage(SysApiTokenPageDTO dto);

    SysApiTokenDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
