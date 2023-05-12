package com.cmcorg20230301.engine.be.pay.wx.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.wx.properties.PayWxProperties;
import com.cmcorg20230301.engine.be.security.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.wechat.pay.java.core.RSAConfig;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import com.wechat.pay.java.service.payments.h5.model.PrepayResponse;
import com.wechat.pay.java.service.payments.h5.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付：支付宝工具类
 */
@Component
public class PayWxUtil {

    /**
     * 商户号
     */
    public static String merchantId = "";

    /**
     * 商户API私钥路径
     */
    public static String privateKeyPath = "";

    /**
     * 商户证书序列号
     */
    public static String merchantSerialNumber = "";

    public static String wechatPayCertificatePath = "";

    private static PayWxProperties payWxProperties;

    public PayWxUtil(PayWxProperties payWxProperties) {

        PayWxUtil.payWxProperties = payWxProperties;

    }

    public static H5Service getH5Service() {

        RSAConfig rsaConfig = new RSAConfig.Builder().merchantId(merchantId)
            // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
            .privateKeyFromPath(privateKeyPath).merchantSerialNumber(merchantSerialNumber)
            .wechatPayCertificatesFromPath(wechatPayCertificatePath).build();

        return new H5Service.Builder().config(rsaConfig).build();

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

        if (compare < 0) {
            ApiResultVO.error("操作失败：支付过期时间晚于当前时间");
        }

        PrepayRequest request = new PrepayRequest();

        Amount amount = new Amount();

        amount.setTotal(dto.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue());

        request.setAmount(amount);

        request.setAppid("wxa9d9651ae******");
        request.setMchid("190000****");
        request.setDescription(dto.getSubject());
        request.setNotifyUrl("https://notify_url");
        request.setOutTradeNo(dto.getOutTradeNo());

        // 调用接口
        PrepayResponse prepayResponse = getH5Service().prepay(request);

        return prepayResponse.getH5Url();

    }

    /**
     * 交易查询接口
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    @SneakyThrows
    public static SysPayTradeStatusEnum query(String outTradeNo) {

        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();

        // 调用接口
        Transaction transaction = getH5Service().queryOrderByOutTradeNo(request);

        Transaction.TradeStateEnum tradeStateEnum = transaction.getTradeState();

        return null;

    }

}
