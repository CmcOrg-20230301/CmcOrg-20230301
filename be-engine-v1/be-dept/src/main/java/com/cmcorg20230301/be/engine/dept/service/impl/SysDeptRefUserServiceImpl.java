package com.cmcorg20230301.be.engine.dept.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.dept.service.SysDeptRefUserService;
import com.cmcorg20230301.be.engine.security.mapper.SysDeptRefUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysDeptRefUserDO;

@Service
public class SysDeptRefUserServiceImpl extends ServiceImpl<SysDeptRefUserMapper, SysDeptRefUserDO>
    implements SysDeptRefUserService {

}
