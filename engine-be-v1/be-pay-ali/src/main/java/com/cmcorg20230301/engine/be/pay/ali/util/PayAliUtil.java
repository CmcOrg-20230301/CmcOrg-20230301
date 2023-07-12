package com.cmcorg20230301.engine.be.pay.ali.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.ali.properties.PayAliProperties;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 支付：支付宝工具类
 */
@Component
public class PayAliUtil {

    private static PayAliProperties payAliProperties;

    public PayAliUtil(PayAliProperties payAliProperties) {

        PayAliUtil.payAliProperties = payAliProperties;

    }

    public static AlipayConfig getAlipayConfig() {

        AlipayConfig alipayConfig = new AlipayConfig();

        alipayConfig.setServerUrl(payAliProperties.getServerUrl());
        alipayConfig.setAppId(payAliProperties.getAppId());
        alipayConfig.setPrivateKey(payAliProperties.getPrivateKey());
        alipayConfig.setAlipayPublicKey(payAliProperties.getPlatformPublicKey());

        return alipayConfig;

    }

    /**
     * 支付
     */
    @SneakyThrows
    public static String pay(PayDTO dto) {

        Assert.notBlank(dto.getOutTradeNo());
        Assert.notNull(dto.getTotalAmount());
        Assert.notBlank(dto.getSubject());

        int compare = DateUtil.compare(dto.getTimeExpire(), new Date());

        if (compare <= 0) {
            ApiResultVO.error("操作失败：支付过期时间晚于当前时间");
        }

        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        AlipayTradePagePayRequest aliPayRequest = new AlipayTradePagePayRequest();
        aliPayRequest.setReturnUrl(payAliProperties.getReturnUrl());
        aliPayRequest.setNotifyUrl(payAliProperties.getNotifyUrl());

        JSONObject bizContent = JSONUtil.createObj();
        bizContent.set("out_trade_no", dto.getOutTradeNo());
        bizContent.set("total_amount", dto.getTotalAmount());
        bizContent.set("subject", dto.getSubject());
        bizContent.set("body", dto.getBody());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.set("time_expire", DateUtil.formatDateTime(dto.getTimeExpire()));

        aliPayRequest.setBizContent(bizContent.toString());

        // 备注：指定为 GET，那么 body就是 url，反之就是：html的 form表单格式
        AlipayTradePagePayResponse response = alipayClient.pageExecute(aliPayRequest, "GET");

        if (BooleanUtil.isFalse(response.isSuccess())) {

            // code，例如：40004
            // msg，例如：Business Failed
            // sub_code，例如：ACQ.TRADE_HAS_SUCCESS
            // sub_msg，例如：交易已被支付
            ApiResultVO.error("支付宝支付失败：" + response.getSubMsg());

        }

        // 备注：这里返回的是 url链接
        return response.getBody();

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    public static SysPayTradeStatusEnum query(String outTradeNo) {

        Assert.notBlank(outTradeNo);

        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", outTradeNo);

        request.setBizContent(bizContent.toString());

        AlipayTradeQueryResponse response = alipayClient.execute(request);

        if (BooleanUtil.isFalse(response.isSuccess())) {

            ApiResultVO.error("支付宝查询失败：" + response.getSubMsg());

        }

        return SysPayTradeStatusEnum.getByStatus(response.getTradeStatus());

    }

}
