package com.cmcorg20230301.engine.be.pay.base.model.configuration;

import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTypeEnum;

public interface ISysPay {

    /**
     * 支付方式类型
     */
    SysPayTypeEnum getSysPayType();

    /**
     * 支付，返回 url
     */
    String pay(PayDTO dto);

    /**
     * 查询订单状态
     */
    SysPayTradeStatusEnum query(String outTradeNo);

}
