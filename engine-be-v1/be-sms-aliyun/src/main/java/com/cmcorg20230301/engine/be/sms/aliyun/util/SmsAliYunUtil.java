package com.cmcorg20230301.engine.be.sms.aliyun.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.sms.aliyun.properties.SmsAliYunProperties;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 阿里云：短信工具类
 */
@Component
public class SmsAliYunUtil {

    public static SmsAliYunProperties smsAliYunProperties;

    public SmsAliYunUtil(SmsAliYunProperties smsAliYunProperties) {

        SmsAliYunUtil.smsAliYunProperties = smsAliYunProperties;

    }

    /**
     * 发送：账号注销
     */
    public static void sendDelete(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendDelete());

    }

    /**
     * 发送：绑定手机
     */
    public static void sendBind(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendBind());

    }

    /**
     * 发送：修改手机
     */
    public static void sendUpdate(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendUpdate());

    }

    /**
     * 发送：修改密码
     */
    public static void sendUpdatePassword(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendUpdatePassword());

    }

    /**
     * 发送：忘记密码
     */
    public static void sendForgetPassword(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendForgetPassword());

    }

    /**
     * 发送：登录短信
     */
    public static void sendSignIn(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendSignIn());

    }

    /**
     * 发送：注册短信
     */
    public static void sendSignUp(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, smsAliYunProperties.getSendSignUp());

    }

    /**
     * 发送：验证码相关
     */
    private static void sendForCode(String phoneNumber, String code, String templateCode) {

        // 备注：第二个元素，表示是：验证码多久过期（分钟）
        String templateParam =
            JSONUtil.createObj().set("code", code).set("expire", BaseConstant.LONG_CODE_EXPIRE_MINUTE).toString();

        // 执行：发送短信
        doSend(templateCode, templateParam, phoneNumber);

    }

    /**
     * 执行：发送短信
     * 注意：不建议直接调用本方法，而是把本方法，再封装一层再调用
     */
    @SneakyThrows
    public static void doSend(String templateCode, String templateParam, String phoneNumber) {

        SendSmsRequest sendSmsRequest =
            SendSmsRequest.builder().phoneNumbers(phoneNumber).signName(smsAliYunProperties.getSignName())
                .templateCode(templateCode).templateParam(templateParam).build();

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(
            Credential.builder().accessKeyId(smsAliYunProperties.getSecretId())
                .accessKeySecret(smsAliYunProperties.getSecretKey()).build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder().region("cn-hangzhou") // Region ID
            .credentialsProvider(provider)
            .overrideConfiguration(ClientOverrideConfiguration.create().setEndpointOverride("dysmsapi.aliyuncs.com"))
            .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);

        // Synchronously get the return value of the API request
        SendSmsResponse resp = response.get();

        SendSmsResponseBody body = resp.getBody();

        // Finally, close the client
        client.close();

        String code = body.getCode();

        if (BooleanUtil.isFalse("OK".equalsIgnoreCase(code))) {
            throw new RuntimeException(StrUtil.format("短信发送失败，code：【{}】，message：【{}】", code, body.getMessage()));
        }

    }

}
