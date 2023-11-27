package com.cmcorg20230301.be.engine.pay.ali.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.BooleanUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 支付：支付宝工具类
 */
@Component
public class PayAliUtil {

    /**
     * 获取：支付相关配置对象
     */
    @NotNull
    public static AlipayConfig getAlipayConfig(SysPayConfigurationDO sysPayConfigurationDO) {

        AlipayConfig alipayConfig = new AlipayConfig();

        alipayConfig.setServerUrl(sysPayConfigurationDO.getServerUrl());
        alipayConfig.setAppId(sysPayConfigurationDO.getAppId());
        alipayConfig.setPrivateKey(sysPayConfigurationDO.getPrivateKey());
        alipayConfig.setAlipayPublicKey(sysPayConfigurationDO.getPlatformPublicKey());

        return alipayConfig;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoPayBO {

        private SysPayConfigurationDO sysPayConfigurationDO;

        private AlipayClient alipayClient;

        private String notifyUrl;

    }

    /**
     * 通用的，执行支付
     */
    @SneakyThrows
    private static SysPayReturnBO doPay(PayDTO dto, Func1<DoPayBO, SysPayReturnBO> func1) {

        SysPayConfigurationDO sysPayConfigurationDO = dto.getSysPayConfigurationDO();

        AlipayConfig alipayConfig = getAlipayConfig(sysPayConfigurationDO);

        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        String notifyUrl = sysPayConfigurationDO.getNotifyUrl() + "/" + sysPayConfigurationDO.getId();

        // 执行支付
        return func1.call(new DoPayBO(sysPayConfigurationDO, alipayClient, notifyUrl));

    }

    /**
     * 当面付：二维码扫描付款
     * 参考地址：https://open.alipay.com/api/apiDebug
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO payQrCode(PayDTO dto) {

        // 执行
        return doPay(dto, doPayBO -> {

            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

            model.setOutTradeNo(dto.getOutTradeNo());
            model.setTotalAmount(PayHelper.getPayTotalAmountStr(dto.getTotalAmount()));
            model.setSubject(dto.getSubject());
            model.setBody(dto.getBody());
            model.setTimeExpire(DateUtil.formatDateTime(dto.getExpireTime()));

            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

            request.setNotifyUrl(doPayBO.getNotifyUrl()); // 设置：异步通知地址
            request.setBizModel(model);

            AlipayTradePrecreateResponse response = doPayBO.getAlipayClient().execute(request);

            // 处理：支付宝的返回值
            handleApiPayResponse(response.isSuccess(), "支付宝支付失败：", response.getSubMsg());

            // 返回：扫码地址
            return new SysPayReturnBO(response.getQrCode(), doPayBO.getSysPayConfigurationDO().getAppId());

        });

    }

    /**
     * 支付宝-手机支付
     */
    @SneakyThrows
    public static SysPayReturnBO payApp(PayDTO dto) {

        // 执行
        return doPay(dto, doPayBO -> {

            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

            model.setOutTradeNo(dto.getOutTradeNo());
            model.setTotalAmount(PayHelper.getPayTotalAmountStr(dto.getTotalAmount()));
            model.setSubject(dto.getSubject());
            model.setBody(dto.getBody());
            model.setTimeExpire(DateUtil.formatDateTime(dto.getExpireTime()));

            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

            request.setNotifyUrl(doPayBO.getNotifyUrl()); // 设置：异步通知地址
            request.setBizModel(model);

            AlipayTradeAppPayResponse response = doPayBO.getAlipayClient().execute(request);

            // 处理：支付宝的返回值
            handleApiPayResponse(response.isSuccess(), "支付宝支付失败：", response.getSubMsg());

            // 返回：调用手机支付需要的参数
            return new SysPayReturnBO(response.getBody(), doPayBO.getSysPayConfigurationDO().getAppId());

        });

    }

    /**
     * 支付宝-电脑网站支付
     */
    @SneakyThrows
    public static SysPayReturnBO payWebPc(PayDTO dto) {

        // 执行
        return doPay(dto, doPayBO -> {

            AlipayTradePagePayModel model = new AlipayTradePagePayModel();

            model.setOutTradeNo(dto.getOutTradeNo());
            model.setTotalAmount(PayHelper.getPayTotalAmountStr(dto.getTotalAmount()));
            model.setSubject(dto.getSubject());
            model.setBody(dto.getBody());
            model.setTimeExpire(DateUtil.formatDateTime(dto.getExpireTime()));

            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

            request.setNotifyUrl(doPayBO.getNotifyUrl()); // 设置：异步通知地址
            request.setBizModel(model);

            // 备注：指定为 GET，那么 body就是 url，反之就是：html的 form表单格式
            AlipayTradePagePayResponse response = doPayBO.getAlipayClient().pageExecute(request, "GET");

            // 处理：支付宝的返回值
            handleApiPayResponse(response.isSuccess(), "支付宝支付失败：", response.getSubMsg());

            // 返回：支付的 url链接
            return new SysPayReturnBO(response.getBody(), doPayBO.getSysPayConfigurationDO().getAppId());

        });

    }

    /**
     * 支付宝-手机网站支付
     */
    @SneakyThrows
    public static SysPayReturnBO payWebApp(PayDTO dto) {

        // 执行
        return doPay(dto, doPayBO -> {

            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

            model.setOutTradeNo(dto.getOutTradeNo());
            model.setTotalAmount(PayHelper.getPayTotalAmountStr(dto.getTotalAmount()));
            model.setSubject(dto.getSubject());
            model.setBody(dto.getBody());
            model.setTimeExpire(DateUtil.formatDateTime(dto.getExpireTime()));

            model.setProductCode("QUICK_WAP_WAY");

            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

            request.setNotifyUrl(doPayBO.getNotifyUrl()); // 设置：异步通知地址
            request.setBizModel(model);

            // 备注：指定为 GET，那么 body就是 url，反之就是：html的 form表单格式
            AlipayTradeWapPayResponse response = doPayBO.getAlipayClient().pageExecute(request, "GET");

            // 处理：支付宝的返回值
            handleApiPayResponse(response.isSuccess(), "支付宝支付失败：", response.getSubMsg());

            // 返回：支付的 url链接
            return new SysPayReturnBO(response.getBody(), doPayBO.getSysPayConfigurationDO().getAppId());

        });

    }

    /**
     * 处理：支付宝的返回值
     */
    private static void handleApiPayResponse(boolean success, String preMsg, String subMsg) {

        if (BooleanUtil.isFalse(success)) {

            // code，例如：40004
            // msg，例如：Business Failed
            // sub_code，例如：ACQ.TRADE_HAS_SUCCESS
            // sub_msg，例如：交易已被支付
            ApiResultVO.errorMsg(preMsg + subMsg);

        }

    }

    /**
     * 通用的，查询订单状态
     *
     * @param outTradeNo 本系统的支付主键 id，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum query(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDO) {

        Assert.notBlank(outTradeNo);

        AlipayConfig alipayConfig = getAlipayConfig(sysPayConfigurationDO);

        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        model.setOutTradeNo(outTradeNo);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        request.setBizModel(model);

        AlipayTradeQueryResponse response = alipayClient.execute(request);

        // 处理：支付宝的返回值
        handleApiPayResponse(response.isSuccess(), "支付宝查询失败：", response.getSubMsg());

        return SysPayTradeStatusEnum.getByStatus(response.getTradeStatus());

    }

}
