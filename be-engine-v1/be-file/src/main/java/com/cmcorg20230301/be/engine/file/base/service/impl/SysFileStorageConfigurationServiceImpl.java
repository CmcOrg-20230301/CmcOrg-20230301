package com.cmcorg20230301.be.engine.file.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.file.base.mapper.SysFileStorageConfigurationMapper;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileStorageConfigurationDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileStorageConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class SysFileStorageConfigurationServiceImpl
    extends ServiceImpl<SysFileStorageConfigurationMapper, SysFileStorageConfigurationDO>
    implements SysFileStorageConfigurationService {

}
