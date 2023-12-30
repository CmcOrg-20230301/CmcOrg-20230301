package com.cmcorg20230301.be.engine.sms.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.sms.base.model.dto.SysSmsConfigurationPageDTO;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;

public interface SysSmsConfigurationService extends IService<SysSmsConfigurationDO> {

    String insertOrUpdate(SysSmsConfigurationInsertOrUpdateDTO dto);

    Page<SysSmsConfigurationDO> myPage(SysSmsConfigurationPageDTO dto);

    SysSmsConfigurationDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
