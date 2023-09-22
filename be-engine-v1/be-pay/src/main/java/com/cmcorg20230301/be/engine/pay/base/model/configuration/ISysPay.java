package com.cmcorg20230301.be.engine.pay.base.model.configuration;

import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;

public interface ISysPay {

    /**
     * 支付方式类型
     */
    SysPayTypeEnum getSysPayType();

    /**
     * 支付返回值，备注：一般返回 url
     */
    SysPayReturnBO pay(PayDTO dto);

    /**
     * 查询订单状态
     */
    SysPayTradeStatusEnum query(String outTradeNo, Long tenantId);

}
