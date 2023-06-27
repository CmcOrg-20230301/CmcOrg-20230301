package com.cmcorg20230301.engine.be.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.security.mapper.BaseSysRequestMapper;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;
import com.cmcorg20230301.engine.be.security.service.BaseSysRequestService;
import org.springframework.stereotype.Service;

@Service
public class BaseSysRequestServiceImpl extends ServiceImpl<BaseSysRequestMapper, SysRequestDO>
    implements BaseSysRequestService {

}
