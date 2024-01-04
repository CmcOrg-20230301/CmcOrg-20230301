package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;

public interface SysImSessionRefUserService extends IService<SysImSessionRefUserDO> {

    String joinUserIdSet(NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet);

}
