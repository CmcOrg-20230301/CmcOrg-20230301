package com.cmcorg20230301.be.engine.email.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.email.model.dto.SysEmailConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.email.model.entity.SysEmailConfigurationDO;

public interface SysEmailConfigurationService extends IService<SysEmailConfigurationDO> {

    String insertOrUpdate(SysEmailConfigurationInsertOrUpdateDTO dto);

    SysEmailConfigurationDO infoById();

}
