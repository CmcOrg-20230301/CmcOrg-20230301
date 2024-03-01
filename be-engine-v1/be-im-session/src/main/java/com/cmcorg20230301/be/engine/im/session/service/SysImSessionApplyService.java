package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;

public interface SysImSessionApplyService extends IService<SysImSessionApplyDO> {

    String privateChatApply(NotNullId notNullId);

    String privateChatAgree(NotEmptyIdSet notEmptyIdSet);

}