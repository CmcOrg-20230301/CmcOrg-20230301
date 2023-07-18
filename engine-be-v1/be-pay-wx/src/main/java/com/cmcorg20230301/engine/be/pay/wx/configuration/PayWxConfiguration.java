package com.cmcorg20230301.engine.be.pay.wx.configuration;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.engine.be.pay.wx.properties.PayWxProperties;
import com.cmcorg20230301.engine.be.pay.wx.util.PayWxUtil;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 微信支付相关配置类
 */
@Configuration
public class PayWxConfiguration implements ISysPay {

    @Resource
    PayWxProperties payWxProperties;

    /**
     * 配置类
     */
    @Bean
    public RSAAutoCertificateConfig rsaAutoCertificateConfig() {

        if (StrUtil.isBlank(payWxProperties.getPrivateKey())) {
            return null;
        }

        return new RSAAutoCertificateConfig.Builder().merchantId(payWxProperties.getMerchantId())
            .privateKey(payWxProperties.getPrivateKey()).merchantSerialNumber(payWxProperties.getMerchantSerialNumber())
            .apiV3Key(payWxProperties.getApiV3Key()).build();

    }

    /**
     * Native支付是指商户系统按微信支付协议生成支付二维码，用户再用微信“扫一扫”完成支付的模式。
     * Native支付适用于PC网站、实体店单品或订单、媒体广告支付等场景
     */
    @Bean
    public NativePayService nativePayService() {

        RSAAutoCertificateConfig rsaAutoCertificateConfig = rsaAutoCertificateConfig();

        if (rsaAutoCertificateConfig == null) {
            return null;
        }

        return new NativePayService.Builder().config(rsaAutoCertificateConfig).build();

    }

    /**
     * 解析回调通知的
     */
    @Bean
    public NotificationParser notificationParser() {

        RSAAutoCertificateConfig rsaAutoCertificateConfig = rsaAutoCertificateConfig();

        if (rsaAutoCertificateConfig == null) {
            return null;
        }

        return new NotificationParser(rsaAutoCertificateConfig);

    }

    /**
     * 支付方式类型
     */
    @Override
    public SysPayTypeEnum getSysPayType() {
        return SysPayTypeEnum.WX;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    public String pay(PayDTO dto) {
        return PayWxUtil.payNative(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    public SysPayTradeStatusEnum query(String outTradeNo) {
        return PayWxUtil.queryNative(outTradeNo);
    }

}
