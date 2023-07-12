package com.cmcorg20230301.engine.be.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.base.properties.SysPayProperties;
import com.cmcorg20230301.engine.be.security.model.configuration.IPay;
import com.cmcorg20230301.engine.be.security.model.enums.SysPayTradeStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PayUtil {

    private static SysPayProperties sysPayProperties;

    private static final Map<Integer, IPay> PAY_MAP = MapUtil.newHashMap();

    public PayUtil(SysPayProperties sysPayProperties, @Autowired(required = false) List<IPay> iPayList) {

        PayUtil.sysPayProperties = sysPayProperties;

        if (CollUtil.isNotEmpty(iPayList)) {

            for (IPay item : iPayList) {

                PAY_MAP.put(item.getType(), item);

            }

        }

    }

    /**
     * 支付，返回 url
     */
    public static String pay(PayDTO dto) {

        IPay iPay = PAY_MAP.get(sysPayProperties.getBasePayType());

        if (iPay == null) {

            return null;

        }

        return iPay.pay(dto);

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    public static SysPayTradeStatusEnum query(String outTradeNo) {

        IPay iPay = PAY_MAP.get(sysPayProperties.getBasePayType());

        if (iPay == null) {

            return null;

        }

        return iPay.query(outTradeNo);

    }

}
