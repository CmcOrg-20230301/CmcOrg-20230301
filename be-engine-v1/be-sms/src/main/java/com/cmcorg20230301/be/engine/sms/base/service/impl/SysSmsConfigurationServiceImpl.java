package com.cmcorg20230301.be.engine.sms.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.sms.base.mapper.SysSmsConfigurationMapper;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.service.SysSmsConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class SysSmsConfigurationServiceImpl extends ServiceImpl<SysSmsConfigurationMapper, SysSmsConfigurationDO>
    implements SysSmsConfigurationService {

}
