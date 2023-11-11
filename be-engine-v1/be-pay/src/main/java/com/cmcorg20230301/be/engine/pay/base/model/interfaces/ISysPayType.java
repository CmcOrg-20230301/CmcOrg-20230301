package com.cmcorg20230301.be.engine.pay.base.model.interfaces;

import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationInsertOrUpdateDTO;

import java.util.function.Consumer;

public interface ISysPayType {

    int getCode(); // 建议从：10001（包含）开始

    Consumer<SysPayConfigurationInsertOrUpdateDTO> getCheckSysPayConfigurationInsertOrUpdateDtoConsumer();

}
