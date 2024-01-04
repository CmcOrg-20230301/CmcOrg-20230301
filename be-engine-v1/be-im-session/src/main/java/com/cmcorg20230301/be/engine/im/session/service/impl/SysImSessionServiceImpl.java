package com.cmcorg20230301.be.engine.im.session.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import org.springframework.stereotype.Service;

@Service
public class SysImSessionServiceImpl extends ServiceImpl<SysImSessionMapper, SysImSessionDO>
        implements SysImSessionService {

}
