package com.cmcorg20230301.be.engine.param.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.param.model.dto.SysParamInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.param.model.dto.SysParamPageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.SysParamDO;

public interface SysParamService extends IService<SysParamDO> {

    String insertOrUpdate(SysParamInsertOrUpdateDTO dto);

    Page<SysParamDO> myPage(SysParamPageDTO dto);

    SysParamDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
