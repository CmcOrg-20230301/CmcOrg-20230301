package com.cmcorg20230301.be.engine.pay.base.model.configuration;

import org.jetbrains.annotations.NotNull;

import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;

public interface ISysPay {

    /**
     * 支付方式类型
     */
    @NotNull
    ISysPayType getSysPayType();

    /**
     * 支付返回值，备注：一般返回 url
     */
    @NotNull
    SysPayReturnBO pay(PayDTO dto);

    /**
     * 查询订单状态
     */
    @NotNull
    SysPayTradeStatusEnum query(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDO);

}
