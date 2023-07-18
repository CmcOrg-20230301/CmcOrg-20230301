package com.cmcorg20230301.engine.be.pay.wx.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.wx.properties.PayWxProperties;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 支付：微信工具类
 */
@Component
public class PayWxUtil {

    private static PayWxProperties payWxProperties;

    @Resource
    public void setPayWxProperties(PayWxProperties payWxProperties) {
        PayWxUtil.payWxProperties = payWxProperties;
    }

    private static NativePayService nativePayService;

    @Autowired(required = false)
    public void setNativePayService(NativePayService nativePayService) {
        PayWxUtil.nativePayService = nativePayService;
    }

    private static JsapiServiceExtension jsapiServiceExtension;

    @Autowired(required = false)
    public void setJsapiService(JsapiServiceExtension jsapiServiceExtension) {
        PayWxUtil.jsapiServiceExtension = jsapiServiceExtension;
    }

    /**
     * 支付
     */
    @SneakyThrows
    public static String payNative(PayDTO dto) {

        Assert.notBlank(dto.getOutTradeNo());
        Assert.notNull(dto.getTotalAmount());
        Assert.notBlank(dto.getSubject());

        int compare = DateUtil.compare(dto.getTimeExpire(), new Date());

        if (compare <= 0) {
            ApiResultVO.error("操作失败：支付过期时间晚于当前时间");
        }

        com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request =
            new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();

        com.wechat.pay.java.service.payments.nativepay.model.Amount amount =
            new com.wechat.pay.java.service.payments.nativepay.model.Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BaseConstant.BIG_DECIMAL_ONE_HUNDRED).intValue());

        request.setAmount(amount);

        request.setAppid(payWxProperties.getAppId());
        request.setMchid(payWxProperties.getMerchantId());
        request.setDescription(dto.getSubject());
        request.setNotifyUrl(payWxProperties.getNotifyUrl());
        request.setOutTradeNo(dto.getOutTradeNo());
        request.setTimeExpire(DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.format(dto.getTimeExpire()));

        // 调用接口
        com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse prepayResponse =
            nativePayService.prepay(request);

        return prepayResponse.getCodeUrl();

    }

    /**
     * 获取：jsApi调起支付需要的参数
     */
    @SneakyThrows
    public static Object payJsApi(PayDTO dto) {

        Assert.notBlank(dto.getOutTradeNo());
        Assert.notNull(dto.getTotalAmount());
        Assert.notBlank(dto.getSubject());
        Assert.notBlank(dto.getOpenId());

        int compare = DateUtil.compare(dto.getTimeExpire(), new Date());

        if (compare <= 0) {
            ApiResultVO.error("操作失败：支付过期时间晚于当前时间");
        }

        com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest request =
            new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();

        com.wechat.pay.java.service.payments.jsapi.model.Amount amount =
            new com.wechat.pay.java.service.payments.jsapi.model.Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BaseConstant.BIG_DECIMAL_ONE_HUNDRED).intValue());

        request.setAmount(amount);

        request.setAppid(payWxProperties.getAppId());
        request.setMchid(payWxProperties.getMerchantId());
        request.setDescription(dto.getSubject());
        request.setNotifyUrl(payWxProperties.getNotifyUrl());
        request.setOutTradeNo(dto.getOutTradeNo());
        request.setTimeExpire(DatePattern.UTC_WITH_XXX_OFFSET_FORMAT.format(dto.getTimeExpire()));

        com.wechat.pay.java.service.payments.jsapi.model.Payer payer =
            new com.wechat.pay.java.service.payments.jsapi.model.Payer();

        payer.setOpenid(dto.getOpenId());

        request.setPayer(payer);

        // 执行
        return jsapiServiceExtension.prepayWithRequestPayment(request);

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    public static SysPayTradeStatusEnum queryNative(String outTradeNo) {

        com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest queryRequest =
            new com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest();

        queryRequest.setMchid(payWxProperties.getMerchantId());
        queryRequest.setOutTradeNo(outTradeNo);

        // 调用接口
        Transaction transaction = nativePayService.queryOrderByOutTradeNo(queryRequest);

        return SysPayTradeStatusEnum.getByStatus(transaction.getTradeState().name());

    }

}
