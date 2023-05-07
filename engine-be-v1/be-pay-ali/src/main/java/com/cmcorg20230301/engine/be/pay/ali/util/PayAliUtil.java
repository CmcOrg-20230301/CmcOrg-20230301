package com.cmcorg20230301.engine.be.pay.ali.util;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.api.AlipayTradeApi;
import com.alipay.v3.model.*;
import com.alipay.v3.util.model.AlipayConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * 支付：支付宝工具类
 */
@Component
public class PayAliUtil {

    @SneakyThrows
    public static void main(String[] args) {

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://openapi.alipay.com");
        // 设置alipayConfig参数（全局设置一次）
        AlipayConfig config = new AlipayConfig();
        config.setAppId("app_id");
        config.setPrivateKey("private_key");
        // 密钥模式
        config.setAlipayPublicKey("alipay_public_key");
        // 证书模式
        // config.setAppCertPath("../appCertPublicKey.crt");
        // config.setAlipayPublicCertPath("../alipayCertPublicKey_RSA2.crt");
        // config.setRootCertPath("../alipayRootCert.crt");
        config.setEncryptKey("encrypt_key");
        defaultClient.setAlipayConfig(config);

        //实例化客户端
        AlipayTradeApi api = new AlipayTradeApi();
        //调用 alipay.trade.pay
        AlipayTradePayModel alipayTradePayModel =
            new AlipayTradePayModel().outTradeNo("20210817010101001").totalAmount("0.01").subject("测试商品")
                .scene("bar_code").authCode("28763443825664394");
        //发起调用
        try {
            AlipayTradePayResponseModel response = api.pay(alipayTradePayModel);
        } catch (ApiException e) {
            AlipayTradePayDefaultResponse errorObject = (AlipayTradePayDefaultResponse)e.getErrorObject();
            if (errorObject != null && errorObject.getActualInstance() instanceof CommonErrorType) {
                //获取公共错误码
                CommonErrorType actualInstance = errorObject.getCommonErrorType();
                System.out.println("CommonErrorType:" + actualInstance.toString());
            } else if (errorObject != null && errorObject
                .getActualInstance() instanceof AlipayTradePayErrorResponseModel) {
                //获取业务错误码
                AlipayTradePayErrorResponseModel actualInstance = errorObject.getAlipayTradePayErrorResponseModel();
                System.out.println("AlipayTradePayErrorResponseModel:" + actualInstance.toString());
            } else {
                //获取其他报错（如加验签失败、http请求失败等）
                System.err.println("Status code: " + e.getCode());
                System.err.println("Reason: " + e.getResponseBody());
                System.err.println("Response headers: " + e.getResponseHeaders());
            }
        }

    }

}
