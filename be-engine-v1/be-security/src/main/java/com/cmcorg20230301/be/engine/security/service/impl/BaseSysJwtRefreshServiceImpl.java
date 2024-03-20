package com.cmcorg20230301.be.engine.security.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.security.mapper.SysJwtRefreshMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysJwtRefreshDO;
import com.cmcorg20230301.be.engine.security.service.BaseSysJwtRefreshService;

@Service
public class BaseSysJwtRefreshServiceImpl extends ServiceImpl<SysJwtRefreshMapper, SysJwtRefreshDO>
    implements BaseSysJwtRefreshService {

}
