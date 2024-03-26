package com.cmcorg20230301.be.engine.security.service;

import com.cmcorg20230301.be.engine.security.model.dto.SysLogPushDTO;

public interface SysLogService {

    String push(SysLogPushDTO dto);

}
