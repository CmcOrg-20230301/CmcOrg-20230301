package com.cmcorg20230301.be.engine.tenant.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantRefUserDO;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantRefUserService;

@Service
public class SysTenantRefUserServiceImpl extends ServiceImpl<SysTenantRefUserMapper, SysTenantRefUserDO>
    implements SysTenantRefUserService {

}
