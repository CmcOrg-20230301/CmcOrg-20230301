package com.cmcorg20230301.engine.be.param.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.param.dto.SysParamInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.param.dto.SysParamPageDTO;
import com.cmcorg20230301.engine.be.security.model.entity.SysParamDO;

public interface SysParamService extends IService<SysParamDO> {

    String insertOrUpdate(SysParamInsertOrUpdateDTO dto);

    Page<SysParamDO> myPage(SysParamPageDTO dto);

    SysParamDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
