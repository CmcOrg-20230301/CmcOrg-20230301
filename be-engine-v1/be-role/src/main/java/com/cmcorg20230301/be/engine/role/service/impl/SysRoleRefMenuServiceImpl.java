package com.cmcorg20230301.be.engine.role.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefMenuService;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleRefMenuMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysRoleRefMenuDO;

@Service
public class SysRoleRefMenuServiceImpl extends ServiceImpl<SysRoleRefMenuMapper, SysRoleRefMenuDO>
    implements SysRoleRefMenuService {

}
