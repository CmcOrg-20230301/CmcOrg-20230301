package com.cmcorg20230301.be.engine.file.base.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.file.base.mapper.SysFileAuthMapper;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileAuthDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileAuthService;

@Service
public class SysFileAuthServiceImpl extends ServiceImpl<SysFileAuthMapper, SysFileAuthDO>
    implements SysFileAuthService {

}
