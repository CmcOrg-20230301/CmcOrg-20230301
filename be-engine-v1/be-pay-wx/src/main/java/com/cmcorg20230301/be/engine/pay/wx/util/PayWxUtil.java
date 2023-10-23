package com.cmcorg20230301.be.engine.pay.wx.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * 支付：微信工具类
 */
@Component
public class PayWxUtil {

    /**
     * 获取：RSAAutoCertificateConfig 对象
     */
    public static RSAAutoCertificateConfig getRsaAutoCertificateConfig(@Nullable Long tenantId,
        @Nullable CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack, SysPayTypeEnum sysPayTypeEnum,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        SysPayConfigurationDO sysPayConfigurationDO;

        if (sysPayConfigurationDoTemp == null) {

            sysPayConfigurationDO = PayHelper.getSysPayConfigurationDO(tenantId, sysPayTypeEnum);

        } else {

            sysPayConfigurationDO = sysPayConfigurationDoTemp;

        }

        if (sysPayConfigurationDoCallBack != null) {

            // 设置：回调值
            sysPayConfigurationDoCallBack.setValue(sysPayConfigurationDO);

        }

        return new RSAAutoCertificateConfig.Builder().merchantId(sysPayConfigurationDO.getMerchantId())
            .privateKey(sysPayConfigurationDO.getPrivateKey())
            .merchantSerialNumber(sysPayConfigurationDO.getMerchantSerialNumber())
            .apiV3Key(sysPayConfigurationDO.getApiV3Key()).build();

    }

    /**
     * 获取：NativePayService 对象
     */
    private static NativePayService getNativePayService(@Nullable Long tenantId,
        @Nullable CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        RSAAutoCertificateConfig rsaAutoCertificateConfig =
            getRsaAutoCertificateConfig(tenantId, sysPayConfigurationDoCallBack, SysPayTypeEnum.WX_NATIVE,
                sysPayConfigurationDoTemp);

        return new NativePayService.Builder().config(rsaAutoCertificateConfig).build();

    }

    /**
     * 支付-native
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO payNative(PayDTO dto) {

        CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack = new CallBack<>();

        NativePayService nativePayService =
            getNativePayService(dto.getTenantId(), sysPayConfigurationDoCallBack, dto.getSysPayConfigurationDoTemp());

        dto.setSysPayConfigurationDoTemp(sysPayConfigurationDoCallBack.getValue());

        com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request =
            new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();

        com.wechat.pay.java.service.payments.nativepay.model.Amount amount =
            new com.wechat.pay.java.service.payments.nativepay.model.Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BaseConstant.BIG_DECIMAL_ONE_HUNDRED).intValue());

        request.setAmount(amount);

        request.setAppid(sysPayConfigurationDoCallBack.getValue().getAppId());
        request.setMchid(sysPayConfigurationDoCallBack.getValue().getMerchantId());
        request.setDescription(dto.getSubject());

        request.setNotifyUrl(sysPayConfigurationDoCallBack.getValue().getNotifyUrl() + "/" + dto.getTenantId() + "/"
            + sysPayConfigurationDoCallBack.getValue().getId());

        request.setOutTradeNo(dto.getOutTradeNo());
        request.setTimeExpire(DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.format(dto.getExpireTime()));

        // 调用接口
        com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse prepayResponse =
            nativePayService.prepay(request);

        return new SysPayReturnBO(prepayResponse.getCodeUrl(), sysPayConfigurationDoCallBack.getValue().getAppId());

    }

    /**
     * 查询订单状态-native
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum queryNative(String outTradeNo, Long tenantId,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack = new CallBack<>();

        NativePayService nativePayService =
            getNativePayService(tenantId, sysPayConfigurationDoCallBack, sysPayConfigurationDoTemp);

        com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest queryRequest =
            new com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest();

        queryRequest.setMchid(sysPayConfigurationDoCallBack.getValue().getMerchantId());
        queryRequest.setOutTradeNo(outTradeNo);

        // 调用接口
        Transaction transaction = nativePayService.queryOrderByOutTradeNo(queryRequest);

        return SysPayTradeStatusEnum.getByStatus(transaction.getTradeState().name());

    }

    /**
     * 获取：JsapiServiceExtension 对象
     */
    private static JsapiServiceExtension getJsapiServiceExtension(@Nullable Long tenantId,
        @Nullable CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        RSAAutoCertificateConfig rsaAutoCertificateConfig =
            getRsaAutoCertificateConfig(tenantId, sysPayConfigurationDoCallBack, SysPayTypeEnum.WX_JSAPI,
                sysPayConfigurationDoTemp);

        return new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();

    }

    /**
     * 支付-jsApi：jsApi调起支付需要的参数
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO payJsApi(PayDTO dto) {

        Assert.notBlank(dto.getOpenId());

        CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack = new CallBack<>();

        JsapiServiceExtension jsapiServiceExtension =
            getJsapiServiceExtension(dto.getTenantId(), sysPayConfigurationDoCallBack,
                dto.getSysPayConfigurationDoTemp());

        dto.setSysPayConfigurationDoTemp(sysPayConfigurationDoCallBack.getValue());

        com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request =
            new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();

        com.wechat.pay.java.service.payments.jsapi.model.Amount amount =
            new com.wechat.pay.java.service.payments.jsapi.model.Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BaseConstant.BIG_DECIMAL_ONE_HUNDRED).intValue());

        request.setAmount(amount);

        request.setAppid(sysPayConfigurationDoCallBack.getValue().getAppId());
        request.setMchid(sysPayConfigurationDoCallBack.getValue().getMerchantId());
        request.setDescription(dto.getSubject());

        request.setNotifyUrl(sysPayConfigurationDoCallBack.getValue().getNotifyUrl() + "/" + dto.getTenantId() + "/"
            + sysPayConfigurationDoCallBack.getValue().getId());

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

        return new SysPayReturnBO(jsonStr, sysPayConfigurationDoCallBack.getValue().getAppId());

    }

    /**
     * 查询订单状态-jsApi
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum queryJsApi(String outTradeNo, Long tenantId,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack = new CallBack<>();

        JsapiServiceExtension jsapiServiceExtension =
            getJsapiServiceExtension(tenantId, sysPayConfigurationDoCallBack, sysPayConfigurationDoTemp);

        com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest queryRequest =
            new com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest();

        queryRequest.setMchid(sysPayConfigurationDoCallBack.getValue().getMerchantId());
        queryRequest.setOutTradeNo(outTradeNo);

        // 调用接口
        Transaction transaction = jsapiServiceExtension.queryOrderByOutTradeNo(queryRequest);

        return SysPayTradeStatusEnum.getByStatus(transaction.getTradeState().name());

    }

}
