package com.cmcorg20230301.be.engine.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.security.mapper.SysUserSingleSignInMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserSingleSignInDO;
import com.cmcorg20230301.be.engine.security.service.SysUserSingleSignInService;
import org.springframework.stereotype.Service;

@Service
public class SysUserSingleSignInServiceImpl extends
    ServiceImpl<SysUserSingleSignInMapper, SysUserSingleSignInDO>
    implements SysUserSingleSignInService {

}
