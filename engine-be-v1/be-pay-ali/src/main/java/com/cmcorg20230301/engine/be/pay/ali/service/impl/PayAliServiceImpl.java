package com.cmcorg20230301.engine.be.pay.ali.service.impl;

import com.alipay.api.AlipayConfig;
import com.alipay.api.internal.util.AlipaySignature;
import com.cmcorg20230301.engine.be.pay.ali.service.PayAliService;
import com.cmcorg20230301.engine.be.pay.ali.util.PayAliUtil;
import com.cmcorg20230301.engine.be.pay.base.model.bo.TradeNotifyBO;
import com.cmcorg20230301.engine.be.pay.base.util.PayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class PayAliServiceImpl implements PayAliService {

    /**
     * 服务器异步通知，备注：第三方应用调用
     */
    @SneakyThrows
    @Override
    public String notifyCallBack(HttpServletRequest request) {

        Map<String, String> paramsMap = new HashMap<>();

        Map<String, String[]> requestParamMap = request.getParameterMap();

        for (Iterator<String> iter = requestParamMap.keySet().iterator(); iter.hasNext(); ) {

            String name = iter.next();

            String[] values = requestParamMap.get(name);

            String valueStr = "";

            for (int i = 0; i < values.length; i++) {

                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";

            }

            paramsMap.put(name, valueStr);

        }

        AlipayConfig alipayConfig = PayAliUtil.getAlipayConfig();

        boolean signVerified = AlipaySignature
            .rsaCheckV1(paramsMap, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(),
                alipayConfig.getSignType()); // 调用SDK验证签名

        if (signVerified) {

            // 商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);

            // 支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);

            // 付款金额
            String totalAmount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);

            // 交易状态
            String tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);

            TradeNotifyBO tradeNotifyBO = new TradeNotifyBO();

            tradeNotifyBO.setTradeStatus(tradeStatus);
            tradeNotifyBO.setOutTradeNo(outTradeNo);
            tradeNotifyBO.setTradeNo(tradeNo);
            tradeNotifyBO.setTotalAmount(totalAmount);
            tradeNotifyBO.setPayCurrency("CNY");

            // 处理：订单
            PayUtil.handleTrade(tradeNotifyBO);

        }

        return "success"; // 备注：这里一直都返回 success，原因：如果返回 failure，则支付宝那边会再次回调，没有这个必要

    }

}
