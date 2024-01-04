package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionInsertOrUpDateDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;

public interface SysImSessionService extends IService<SysImSessionDO> {

    String insertOrUpdate(SysImSessionInsertOrUpDateDTO dto);

}
