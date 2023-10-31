package com.cmcorg20230301.be.engine.other.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppPageDTO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;

public interface SysOtherAppService extends IService<SysOtherAppDO> {

    String insertOrUpdate(SysOtherAppInsertOrUpdateDTO dto);

    Page<SysOtherAppDO> myPage(SysOtherAppPageDTO dto);

    SysOtherAppDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    String getNameById(NotNullId notNullId);

}
