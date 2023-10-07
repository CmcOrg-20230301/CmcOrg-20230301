package com.cmcorg20230301.be.engine.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.security.model.dto.SysUserConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserConfigurationDO;

public interface SysUserConfigurationService extends IService<SysUserConfigurationDO> {

    SysUserConfigurationDO getSysUserConfigurationDoByTenantId(Long tenantId);

    String insertOrUpdate(SysUserConfigurationInsertOrUpdateDTO dto);

    SysUserConfigurationDO infoById();

}
