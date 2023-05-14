package com.cmcorg20230301.engine.be.pay.wx.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.pay.wx.service.PayWxService;
import com.cmcorg20230301.engine.be.security.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.security.util.ResponseUtil;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
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
     * 服务器异步通知-Native，备注：第三方应用调用
     */
    @SneakyThrows
    @Override
    public void notifyCallBackNative(HttpServletRequest request, HttpServletResponse response) {

        try {

            String signature = request.getHeader("Wechatpay-Signature");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String serial = request.getHeader("Wechatpay-Serial");
            String signatureType = request.getHeader("Wechatpay-Signature-Type");

            ServletInputStream inputStream = request.getInputStream();

            String body = IoUtil.readUtf8(inputStream);

            // 构造 RequestParam
            RequestParam requestParam =
                new RequestParam.Builder().serialNumber(serial).nonce(nonce).signature(signature)
                    .signType(signatureType).timestamp(timestamp).body(body).build();

            // 以支付通知回调为例，验签、解密并转换成 Transaction
            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);

            SysPayTradeStatusEnum sysPayTradeStatusEnum =
                SysPayTradeStatusEnum.getByCode(transaction.getTradeState().name());

            if (SysPayTradeStatusEnum.TRADE_SUCCESS.equals(sysPayTradeStatusEnum)) {

                // 支付成功，处理业务

                return;

            }

            ResponseUtil
                .out(response, JSONUtil.createObj().set("code", "FAIL").set("message", "操作失败：订单状态不是支付成功").toString(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (Exception e) {

            ResponseUtil.out(response, JSONUtil.createObj().set("code", "FAIL").set("message", "操作失败：验签异常").toString(),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }

    }

}
