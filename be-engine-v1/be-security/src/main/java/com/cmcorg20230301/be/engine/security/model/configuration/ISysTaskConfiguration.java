package com.cmcorg20230301.be.engine.security.model.configuration;

import com.cmcorg20230301.be.engine.security.model.entity.SysTaskDO;

public interface ISysTaskConfiguration {

    int getCode();

    void handle(SysTaskDO sysTaskDO);

}
