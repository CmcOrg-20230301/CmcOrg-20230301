package com.cmcorg20230301.be.engine.model.properties;

import lombok.Data;

/**
 * 短信服务配置类，基础类
 */
@Data
public class SysSmsBaseProperties {

    /**
     * 密钥对 secretId
     */
    private String secretId;

    /**
     * 密钥对 secretKey
     */
    private String secretKey;

    /**
     * 短信应用ID
     */
    private String sdkAppId;

    /**
     * 短信签名内容
     */
    private String signName;

    /**
     * 发送：账号注销
     */
    private String sendDelete;

    /**
     * 发送：绑定手机
     */
    private String sendBind;

    /**
     * 发送：修改手机
     */
    private String sendUpdate;

    /**
     * 发送：修改密码
     */
    private String sendUpdatePassword;

    /**
     * 发送：忘记密码
     */
    private String sendForgetPassword;

    /**
     * 发送：登录短信
     */
    private String sendSignIn;

    /**
     * 发送：注册短信
     */
    private String sendSignUp;

}
