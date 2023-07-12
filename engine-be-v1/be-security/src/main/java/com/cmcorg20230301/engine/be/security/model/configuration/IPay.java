package com.cmcorg20230301.engine.be.security.model.configuration;

import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.security.model.enums.SysPayTradeStatusEnum;

public interface IPay {

    /**
     * 1 支付宝 2 微信 3 云闪付
     */
    int getType();

    /**
     * 支付，返回 url
     */
    String pay(PayDTO dto);

    /**
     * 查询订单状态
     */
    SysPayTradeStatusEnum query(String outTradeNo);

}
