package com.cmcorg20230301.be.engine.post.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.post.service.SysPostRefUserService;
import com.cmcorg20230301.be.engine.security.mapper.SysPostRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysPostRefUserDO;

@Service
public class SysPostRefUserServiceImpl extends ServiceImpl<SysPostRefUserMapper, SysPostRefUserDO>
    implements SysPostRefUserService {

}
