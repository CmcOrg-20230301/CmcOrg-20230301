package com.cmcorg20230301.be.engine.pay.apply.service.impl;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.pay.apply.service.PayApplyService;
import com.cmcorg20230301.be.engine.pay.apply.util.PayApplyUtil;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayService;
import com.cmcorg20230301.be.engine.pay.base.util.PayUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = LogTopicConstant.PAY_APPLY)
public class PayApplyServiceImpl implements PayApplyService {

    @Resource
    SysPayService sysPayService;

    /**
     * 服务器异步通知，备注：第三方应用调用
     */
    @Override
    public String notifyCallBack(JSONObject jsonObject) {

        String signedPayloadStr = jsonObject.getStr("signedPayload");

        if (StrUtil.isBlank(signedPayloadStr)) {

            throw new RuntimeException("signedPayload不能为空");

        }

        JSONObject signedPayloadJson = getPayloads(signedPayloadStr);

        String signedTransactionInfoStr = signedPayloadJson.getJSONObject("data").getStr("signedTransactionInfo");

        JSONObject signedTransactionInfoJson = getPayloads(signedTransactionInfoStr);

        String transactionReason = signedTransactionInfoJson.getStr("transactionReason");

        if (!"PURCHASE".equalsIgnoreCase(transactionReason)) {

            log.info("苹果支付，transactionReason 不是 PURCHASE：{}", transactionReason);
            return "success";

        }

        String appAccountToken = signedTransactionInfoJson.getStr("appAccountToken");

        JSONObject appAccountTokenJson = JSONUtil.parseObj(appAccountToken);

        // 获取：本系统订单 id
        String outTradeNo = appAccountTokenJson.getStr(PayApplyUtil.OUT_TRADE_NO);

        if (StrUtil.isBlank(outTradeNo)) {

            log.info("苹果支付，outTradeNo为空，appAccountTokenJson：{}", appAccountTokenJson);
            return "success";

        }

        SysPayDO sysPayDO =
            sysPayService.lambdaQuery().eq(SysPayDO::getId, outTradeNo).select(SysPayDO::getOriginalPrice).one();

        if (sysPayDO == null) {

            log.info("苹果支付，sysPayDO为 null：{}", outTradeNo);
            return "success";

        }

        SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

        sysPayTradeNotifyBO.setTradeStatus(CollUtil.getFirst(SysPayTradeStatusEnum.TRADE_SUCCESS.getStatusSet()));
        sysPayTradeNotifyBO.setOutTradeNo(outTradeNo);
        sysPayTradeNotifyBO.setTradeNo(BaseConstant.NEGATIVE_ONE_STR);
        sysPayTradeNotifyBO.setTotalAmount(sysPayDO.getOriginalPrice().toPlainString());
        sysPayTradeNotifyBO.setPayCurrency("CNY");

        // 处理：订单回调
        PayUtil.handleTradeNotify(sysPayTradeNotifyBO, null);

        return "success";

    }

    @SneakyThrows
    private JSONObject getPayloads(String signedPayload) {

        JWT jwt = JWT.of(signedPayload);

        String x5cListStr = (String)jwt.getHeader("x5c");

        String x5c0 = JSONUtil.toList(x5cListStr, String.class).get(0);

        byte[] x5c0Bytes = java.util.Base64.getDecoder().decode(x5c0);

        CertificateFactory fact = CertificateFactory.getInstance("X.509");

        X509Certificate cer = (X509Certificate)fact.generateCertificate(new ByteArrayInputStream(x5c0Bytes));

        PublicKey publicKey = cer.getPublicKey();

        jwt.setKey(publicKey.getEncoded());

        // 验证 x5c证书
        jwt.verify();

        return jwt.getPayloads();

    }

}
