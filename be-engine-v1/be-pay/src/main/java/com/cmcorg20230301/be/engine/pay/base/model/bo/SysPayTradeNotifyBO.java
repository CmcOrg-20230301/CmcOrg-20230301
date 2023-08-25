package com.cmcorg20230301.be.engine.pay.base.model.bo;

import lombok.Data;

@Data
public class SysPayTradeNotifyBO {

    /**
     * 第三方平台的订单状态
     */
    private String tradeStatus;

    /**
     * 本系统的支付订单号
     */
    private String outTradeNo;

    /**
     * 第三方平台的订单号
     */
    private String tradeNo;

    /**
     * 第三方平台的实际支付金额
     */
    private String totalAmount;

    /**
     * 第三方平台的支付货币单位，例如：人民币 CNY
     */
    private String payCurrency;

}
