package com.cmcorg20230301.be.engine.role.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefUserService;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysRoleRefUserDO;

@Service
public class SysRoleRefUserServiceImpl extends ServiceImpl<SysRoleRefUserMapper, SysRoleRefUserDO>
    implements SysRoleRefUserService {

}
