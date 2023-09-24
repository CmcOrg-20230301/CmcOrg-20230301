package com.cmcorg20230301.be.engine.pay.ali.util;

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
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayConfigurationService;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * 支付：支付宝工具类
 */
@Component
public class PayAliUtil {

    private static SysPayConfigurationService sysPayConfigurationService;

    public PayAliUtil(SysPayConfigurationService sysPayConfigurationService) {

        PayAliUtil.sysPayConfigurationService = sysPayConfigurationService;

    }

    /**
     * 获取：支付相关配置对象
     */
    @NotNull
    public static AlipayConfig getAlipayConfig(@Nullable Long tenantId,
        @Nullable CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        SysPayConfigurationDO sysPayConfigurationDO;

        if (sysPayConfigurationDoTemp == null) {

            sysPayConfigurationDO = PayHelper.getSysPayConfigurationDO(tenantId, SysPayTypeEnum.ALI);

        } else {

            sysPayConfigurationDO = sysPayConfigurationDoTemp;

        }

        if (sysPayConfigurationDoCallBack != null) {

            // 设置：回调值
            sysPayConfigurationDoCallBack.setValue(sysPayConfigurationDO);

        }

        AlipayConfig alipayConfig = new AlipayConfig();

        alipayConfig.setServerUrl(sysPayConfigurationDO.getServerUrl());
        alipayConfig.setAppId(sysPayConfigurationDO.getAppId());
        alipayConfig.setPrivateKey(sysPayConfigurationDO.getPrivateKey());
        alipayConfig.setAlipayPublicKey(sysPayConfigurationDO.getPlatformPublicKey());

        return alipayConfig;

    }

    /**
     * 支付
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO pay(PayDTO dto) {

        CallBack<SysPayConfigurationDO> sysPayConfigurationDoCallBack = new CallBack<>();

        AlipayClient alipayClient = new DefaultAlipayClient(
            getAlipayConfig(dto.getTenantId(), sysPayConfigurationDoCallBack, dto.getSysPayConfigurationDoTemp()));

        AlipayTradePagePayRequest aliPayRequest = new AlipayTradePagePayRequest();

        aliPayRequest.setNotifyUrl(
            sysPayConfigurationDoCallBack.getValue().getNotifyUrl() + "/" + dto.getTenantId() + "/"
                + sysPayConfigurationDoCallBack.getValue().getId());

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
            ApiResultVO.errorMsg("支付宝支付失败：" + response.getSubMsg());

        }

        // 备注：response.getBody() 返回的是 url链接
        return new SysPayReturnBO(response.getBody(), sysPayConfigurationDoCallBack.getValue().getAppId());

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum query(String outTradeNo, Long tenantId,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {

        Assert.notBlank(outTradeNo);

        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig(tenantId, null, sysPayConfigurationDoTemp));

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", outTradeNo);

        request.setBizContent(bizContent.toString());

        AlipayTradeQueryResponse response = alipayClient.execute(request);

        if (BooleanUtil.isFalse(response.isSuccess())) {

            ApiResultVO.errorMsg("支付宝查询失败：" + response.getSubMsg());

        }

        return SysPayTradeStatusEnum.getByStatus(response.getTradeStatus());

    }

}
