package com.cmcorg20230301.engine.be.pay.base.util;

import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.ali.util.PayAliUtil;
import com.cmcorg20230301.engine.be.pay.base.properties.SysPayProperties;
import com.cmcorg20230301.engine.be.security.model.enums.SysPayTradeStatusEnum;
import org.springframework.stereotype.Component;

@Component
public class PayUtil {

    private static SysPayProperties sysPayProperties;

    public PayUtil(SysPayProperties sysPayProperties) {

        PayUtil.sysPayProperties = sysPayProperties;

    }

    /**
     * 支付
     */
    public static String pay(PayDTO dto) {

        if (sysPayProperties.getBasePayType() == 1) { // 1 支付宝 2 微信 3 云闪付

           return PayAliUtil.pay(dto);

        }

        return null;

    }

    /**
     * 交易查询接口
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    public static SysPayTradeStatusEnum query(String outTradeNo) {

        if (sysPayProperties.getBasePayType() == 1) { // 1 支付宝 2 微信 3 云闪付

            PayAliUtil.query(outTradeNo);

        }

        return null;

    }

}
