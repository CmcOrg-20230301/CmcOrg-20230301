package com.cmcorg20230301.be.engine.model.properties;

import lombok.Data;

/**
 * 支付系统配置类，基础类
 */
@Data
public class SysPayBaseProperties {

    /**
     * 网关地址
     * 支付宝线上：https://openapi.alipay.com/gateway.do
     * 支付宝沙箱：https://openapi.alipaydev.com/gateway.do
     */
    private String serverUrl;

    /**
     * 应用的ID
     */
    private String appId;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 支付平台的公钥
     */
    private String platformPublicKey;

    /**
     * 同步跳转地址
     */
    private String returnUrl;

    /**
     * 异步接收地址
     */
    private String notifyUrl;

}
