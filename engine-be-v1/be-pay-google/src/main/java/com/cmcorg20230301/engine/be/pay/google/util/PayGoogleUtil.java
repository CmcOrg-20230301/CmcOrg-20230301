package com.cmcorg20230301.engine.be.pay.google.util;

import cn.hutool.core.lang.Assert;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.google.properties.PayGoogleProperties;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付：谷歌工具类
 */
@Component
public class PayGoogleUtil {

    private static PayGoogleProperties payGoogleProperties;

    @Resource
    public void setPayWxProperties(PayGoogleProperties payGoogleProperties) {
        PayGoogleUtil.payGoogleProperties = payGoogleProperties;
    }

    /**
     * 支付
     */
    @SneakyThrows
    public static String pay(PayDTO dto) {

        // 备注：这里不用返回任何值
        return null;

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    public static SysPayTradeStatusEnum query(String outTradeNo, SysPayTradeNotifyBO sysPayTradeNotifyBO) {

        Assert.notBlank(outTradeNo);

        //        Assert.notBlank(outTradeNo);
        //
        //        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());
        //
        //        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //
        //        JSONObject bizContent = new JSONObject();
        //        bizContent.set("out_trade_no", outTradeNo);
        //
        //        request.setBizContent(bizContent.toString());
        //
        //        AlipayTradeQueryResponse response = alipayClient.execute(request);
        //
        //        if (BooleanUtil.isFalse(response.isSuccess())) {
        //
        //            ApiResultVO.error("支付宝查询失败：" + response.getSubMsg());
        //
        //        }
        //
        //        return SysPayTradeStatusEnum.getByStatus(response.getTradeStatus());

        // TODO：查询谷歌那边的订单状态

        if (sysPayTradeNotifyBO != null) {

            sysPayTradeNotifyBO.setTradeNo("tradeNo");
            sysPayTradeNotifyBO.setTotalAmount("totalAmount");
            sysPayTradeNotifyBO.setPayCurrency("CNY");

        }

        return null;

    }

}
