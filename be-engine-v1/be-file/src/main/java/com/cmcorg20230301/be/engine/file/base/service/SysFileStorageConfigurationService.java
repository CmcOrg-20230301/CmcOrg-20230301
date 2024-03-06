package com.cmcorg20230301.be.engine.file.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileStorageConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileStorageConfigurationPageDTO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;

public interface SysFileStorageConfigurationService extends
    IService<SysFileStorageConfigurationDO> {

    String insertOrUpdate(SysFileStorageConfigurationInsertOrUpdateDTO dto);

    Page<SysFileStorageConfigurationDO> myPage(SysFileStorageConfigurationPageDTO dto);

    SysFileStorageConfigurationDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
