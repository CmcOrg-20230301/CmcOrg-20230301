package com.cmcorg20230301.be.engine.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.security.mapper.SysApiTokenMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysApiTokenDO;
import com.cmcorg20230301.be.engine.security.service.SysApiTokenService;
import org.springframework.stereotype.Service;

@Service
public class SysApiTokenServiceImpl extends ServiceImpl<SysApiTokenMapper, SysApiTokenDO>
        implements SysApiTokenService {

}
