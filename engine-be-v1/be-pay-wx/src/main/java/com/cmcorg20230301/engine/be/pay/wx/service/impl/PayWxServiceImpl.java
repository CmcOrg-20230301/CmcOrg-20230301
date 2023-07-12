package com.cmcorg20230301.engine.be.pay.wx.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.func.Func1;
import com.cmcorg20230301.engine.be.pay.base.model.bo.TradeNotifyBO;
import com.cmcorg20230301.engine.be.pay.base.util.PayUtil;
import com.cmcorg20230301.engine.be.pay.wx.service.PayWxService;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class PayWxServiceImpl implements PayWxService {

    @Autowired(required = false)
    NotificationParser notificationParser;

    /**
     * 通用的处理：回调参数
     */
    @SneakyThrows
    private void commonHandleNotifyCallBack(HttpServletRequest request, HttpServletResponse response,
        Func1<RequestParam, TradeNotifyBO> func1) {

        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String serial = request.getHeader("Wechatpay-Serial");
        String signatureType = request.getHeader("Wechatpay-Signature-Type");

        ServletInputStream inputStream = request.getInputStream();

        String body = IoUtil.readUtf8(inputStream);

        // 构造 RequestParam
        RequestParam requestParam =
            new RequestParam.Builder().serialNumber(serial).nonce(nonce).signature(signature).signType(signatureType)
                .timestamp(timestamp).body(body).build();

        // 调用方法，获取：订单状态
        TradeNotifyBO tradeNotifyBO = func1.call(requestParam);

        // 处理：订单
        PayUtil.handleTrade(tradeNotifyBO);

    }

    /**
     * 服务器异步通知-native，备注：第三方应用调用
     */
    @Override
    @SneakyThrows
    public void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response) {

        commonHandleNotifyCallBack(request, response, (requestParam) -> {

            // 以支付通知回调为例，验签、解密并转换成 Transaction
            com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction transaction = notificationParser
                .parse(requestParam, com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction.class);

            TradeNotifyBO tradeNotifyBO = new TradeNotifyBO();

            tradeNotifyBO.setTradeStatus(transaction.getTradeState().name());
            tradeNotifyBO.setOutTradeNo(transaction.getOutTradeNo());
            tradeNotifyBO.setTradeNo(transaction.getTransactionId());
            tradeNotifyBO.setTotalAmount(transaction.getAmount().getPayerTotal().toString());
            tradeNotifyBO.setPayCurrency(transaction.getAmount().getPayerCurrency());

            return tradeNotifyBO;

        });

    }

    /**
     * 服务器异步通知-jsApi，备注：第三方应用调用
     */
    @Override
    public void notifyCallBackJsApi(HttpServletRequest request, HttpServletResponse response) {

        commonHandleNotifyCallBack(request, response, (requestParam) -> {

            // 以支付通知回调为例，验签、解密并转换成 Transaction
            com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction transaction = notificationParser
                .parse(requestParam, com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction.class);

            TradeNotifyBO tradeNotifyBO = new TradeNotifyBO();

            tradeNotifyBO.setTradeStatus(transaction.getTradeState().name());
            tradeNotifyBO.setOutTradeNo(transaction.getOutTradeNo());
            tradeNotifyBO.setTradeNo(transaction.getTransactionId());
            tradeNotifyBO.setTotalAmount(transaction.getAmount().getPayerTotal().toString());
            tradeNotifyBO.setPayCurrency(transaction.getAmount().getPayerCurrency());

            return tradeNotifyBO;

        });

    }

}
