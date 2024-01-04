package com.cmcorg20230301.be.engine.pay.wx.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.func.Func1;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.pay.base.util.PayUtil;
import com.cmcorg20230301.be.engine.pay.wx.service.PayWxService;
import com.cmcorg20230301.be.engine.pay.wx.util.PayWxUtil;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class PayWxServiceImpl implements PayWxService {

    /**
     * 通用的处理：回调参数
     */
    @SneakyThrows
    private void commonHandleNotifyCallBack(HttpServletRequest request, HttpServletResponse response,
                                            Func1<RequestParam, SysPayTradeNotifyBO> func1) {

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
        SysPayTradeNotifyBO sysPayTradeNotifyBO = func1.call(requestParam);

        // 处理：订单回调
        PayUtil.handleTradeNotify(sysPayTradeNotifyBO, null);

    }

    /**
     * 服务器异步通知-native，备注：第三方应用调用
     */
    @Override
    @SneakyThrows
    public void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response,
                                     long sysPayConfigurationId) {

        SysPayConfigurationDO sysPayConfigurationDO = PayHelper.getSysPayConfigurationDO(sysPayConfigurationId);

        if (sysPayConfigurationDO == null) {
            return;
        }

        RSAAutoCertificateConfig rsaAutoCertificateConfig =
                PayWxUtil.getRsaAutoCertificateConfig(sysPayConfigurationDO);

        NotificationParser notificationParser = new NotificationParser(rsaAutoCertificateConfig);

        commonHandleNotifyCallBack(request, response, (requestParam) -> {

            // 以支付通知回调为例，验签、解密并转换成 Transaction
            com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction transaction = notificationParser
                    .parse(requestParam, com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction.class);

            SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

            Integer payerTotal = transaction.getAmount().getPayerTotal(); // 微信这里的单位是：分

            String totalAmount = String.valueOf(payerTotal / 100);

            sysPayTradeNotifyBO.setTradeStatus(transaction.getTradeState().name());
            sysPayTradeNotifyBO.setOutTradeNo(transaction.getOutTradeNo());
            sysPayTradeNotifyBO.setTradeNo(transaction.getTransactionId());
            sysPayTradeNotifyBO.setTotalAmount(totalAmount);
            sysPayTradeNotifyBO.setPayCurrency(transaction.getAmount().getPayerCurrency());

            return sysPayTradeNotifyBO;

        });

    }

    /**
     * 服务器异步通知-jsApi，备注：第三方应用调用
     */
    @Override
    public void notifyCallBackJsApi(HttpServletRequest request, HttpServletResponse response,
                                    long sysPayConfigurationId) {

        SysPayConfigurationDO sysPayConfigurationDO = PayHelper.getSysPayConfigurationDO(sysPayConfigurationId);

        if (sysPayConfigurationDO == null) {
            return;
        }

        RSAAutoCertificateConfig rsaAutoCertificateConfig =
                PayWxUtil.getRsaAutoCertificateConfig(sysPayConfigurationDO);

        NotificationParser notificationParser = new NotificationParser(rsaAutoCertificateConfig);

        commonHandleNotifyCallBack(request, response, (requestParam) -> {

            // 以支付通知回调为例，验签、解密并转换成 Transaction
            com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction transaction = notificationParser
                    .parse(requestParam, com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction.class);

            SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

            Integer payerTotal = transaction.getAmount().getPayerTotal(); // 微信这里的单位是：分

            String totalAmount = String.valueOf(payerTotal / 100);

            sysPayTradeNotifyBO.setTradeStatus(transaction.getTradeState().name());
            sysPayTradeNotifyBO.setOutTradeNo(transaction.getOutTradeNo());
            sysPayTradeNotifyBO.setTradeNo(transaction.getTransactionId());
            sysPayTradeNotifyBO.setTotalAmount(totalAmount);
            sysPayTradeNotifyBO.setPayCurrency(transaction.getAmount().getPayerCurrency());

            return sysPayTradeNotifyBO;

        });

    }

}
