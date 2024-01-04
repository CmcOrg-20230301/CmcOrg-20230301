package com.cmcorg20230301.be.engine.pay.wx.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 支付：微信工具类
 */
@Component
public class PayWxUtil {

    /**
     * 获取：RSAAutoCertificateConfig 对象
     */
    public static RSAAutoCertificateConfig getRsaAutoCertificateConfig(SysPayConfigurationDO sysPayConfigurationDO) {

        return new RSAAutoCertificateConfig.Builder().merchantId(sysPayConfigurationDO.getMerchantId())
                .privateKey(sysPayConfigurationDO.getPrivateKey())
                .merchantSerialNumber(sysPayConfigurationDO.getMerchantSerialNumber())
                .apiV3Key(sysPayConfigurationDO.getApiV3Key()).build();

    }

    /**
     * 获取：NativePayService 对象
     */
    private static NativePayService getNativePayService(SysPayConfigurationDO sysPayConfigurationDO) {

        RSAAutoCertificateConfig rsaAutoCertificateConfig = getRsaAutoCertificateConfig(sysPayConfigurationDO);

        return new NativePayService.Builder().config(rsaAutoCertificateConfig).build();

    }

    /**
     * 支付-native
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO payNative(PayDTO dto) {

        SysPayConfigurationDO sysPayConfigurationDO = dto.getSysPayConfigurationDO();

        NativePayService nativePayService = getNativePayService(sysPayConfigurationDO);

        com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request =
                new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();

        com.wechat.pay.java.service.payments.nativepay.model.Amount amount =
                new com.wechat.pay.java.service.payments.nativepay.model.Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BaseConstant.BIG_DECIMAL_ONE_HUNDRED).intValue());

        request.setAmount(amount);

        request.setAppid(sysPayConfigurationDO.getAppId());
        request.setMchid(sysPayConfigurationDO.getMerchantId());
        request.setDescription(dto.getSubject());

        request.setNotifyUrl(sysPayConfigurationDO.getNotifyUrl() + "/" + sysPayConfigurationDO.getId());

        request.setOutTradeNo(dto.getOutTradeNo());
        request.setTimeExpire(DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.format(dto.getExpireTime()));

        // 调用接口
        com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse prepayResponse =
                nativePayService.prepay(request);

        // 返回：扫码的二维码地址
        return new SysPayReturnBO(prepayResponse.getCodeUrl(), sysPayConfigurationDO.getAppId());

    }

    /**
     * 查询订单状态-native
     *
     * @param outTradeNo 本系统的支付主键 id，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum queryNative(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDO) {

        NativePayService nativePayService = getNativePayService(sysPayConfigurationDO);

        com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest queryRequest =
                new com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest();

        queryRequest.setMchid(sysPayConfigurationDO.getMerchantId());
        queryRequest.setOutTradeNo(outTradeNo);

        // 调用接口
        Transaction transaction = nativePayService.queryOrderByOutTradeNo(queryRequest);

        return SysPayTradeStatusEnum.getByStatus(transaction.getTradeState().name());

    }

    /**
     * 获取：JsapiServiceExtension 对象
     */
    private static JsapiServiceExtension getJsapiServiceExtension(SysPayConfigurationDO sysPayConfigurationDO) {

        RSAAutoCertificateConfig rsaAutoCertificateConfig = getRsaAutoCertificateConfig(sysPayConfigurationDO);

        return new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();

    }

    /**
     * 支付-jsApi：jsApi调起支付需要的参数
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO payJsApi(PayDTO dto) {

        Assert.notBlank(dto.getOpenId());

        SysPayConfigurationDO sysPayConfigurationDO = dto.getSysPayConfigurationDO();

        JsapiServiceExtension jsapiServiceExtension = getJsapiServiceExtension(sysPayConfigurationDO);

        com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request =
                new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();

        com.wechat.pay.java.service.payments.jsapi.model.Amount amount =
                new com.wechat.pay.java.service.payments.jsapi.model.Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BaseConstant.BIG_DECIMAL_ONE_HUNDRED).intValue());

        request.setAmount(amount);

        request.setAppid(sysPayConfigurationDO.getAppId());
        request.setMchid(sysPayConfigurationDO.getMerchantId());
        request.setDescription(dto.getSubject());

        request.setNotifyUrl(sysPayConfigurationDO.getNotifyUrl() + "/" + sysPayConfigurationDO.getId());

        request.setOutTradeNo(dto.getOutTradeNo());
        request.setTimeExpire(DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.format(dto.getExpireTime()));

        com.wechat.pay.java.service.payments.jsapi.model.Payer payer =
                new com.wechat.pay.java.service.payments.jsapi.model.Payer();

        payer.setOpenid(dto.getOpenId());

        request.setPayer(payer);

        // 执行
        PrepayWithRequestPaymentResponse prepayWithRequestPaymentResponse =
                jsapiServiceExtension.prepayWithRequestPayment(request);

        String jsonStr = GsonUtil.toJson(prepayWithRequestPaymentResponse);

        return new SysPayReturnBO(jsonStr, sysPayConfigurationDO.getAppId());

    }

    /**
     * 查询订单状态-jsApi
     *
     * @param outTradeNo 本系统的支付主键 id，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum queryJsApi(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDO) {

        JsapiServiceExtension jsapiServiceExtension = getJsapiServiceExtension(sysPayConfigurationDO);

        com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest queryRequest =
                new com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest();

        queryRequest.setMchid(sysPayConfigurationDO.getMerchantId());
        queryRequest.setOutTradeNo(outTradeNo);

        // 调用接口
        Transaction transaction = jsapiServiceExtension.queryOrderByOutTradeNo(queryRequest);

        return SysPayTradeStatusEnum.getByStatus(transaction.getTradeState().name());

    }

}
