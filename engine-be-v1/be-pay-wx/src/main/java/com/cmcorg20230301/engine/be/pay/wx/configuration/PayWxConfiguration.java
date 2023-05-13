package com.cmcorg20230301.engine.be.pay.wx.configuration;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.pay.wx.properties.PayWxProperties;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 微信支付相关配置类
 */
@Configuration
public class PayWxConfiguration {

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

}
