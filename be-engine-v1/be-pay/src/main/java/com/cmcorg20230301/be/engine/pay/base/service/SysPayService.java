package com.cmcorg20230301.be.engine.pay.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;

public interface SysPayService extends IService<SysPayDO> {

    SysPayTradeStatusEnum payTradeStatusById(NotNullId notNullId);

    SysPayTradeStatusEnum payTradeStatusByIdOther(NotNullId notNullId);

}
