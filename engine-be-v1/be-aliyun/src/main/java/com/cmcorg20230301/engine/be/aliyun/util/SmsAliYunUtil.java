package com.cmcorg20230301.engine.be.aliyun.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.cmcorg20230301.engine.be.aliyun.properties.AliYunProperties;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 阿里云：短信工具类
 */
@Component
public class SmsAliYunUtil {

    public static AliYunProperties aliYunProperties;

    public SmsAliYunUtil(AliYunProperties aliYunProperties) {

        SmsAliYunUtil.aliYunProperties = aliYunProperties;

    }

    /**
     * 发送：账号注销
     */
    public static void sendDelete(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendDelete());

    }

    /**
     * 发送：绑定手机
     */
    public static void sendBind(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendBind());

    }

    /**
     * 发送：修改手机
     */
    public static void sendUpdate(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendUpdate());

    }

    /**
     * 发送：修改密码
     */
    public static void sendUpdatePassword(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendUpdatePassword());

    }

    /**
     * 发送：忘记密码
     */
    public static void sendForgetPassword(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendForgetPassword());

    }

    /**
     * 发送：登录短信
     */
    public static void sendSignIn(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendSignIn());

    }

    /**
     * 发送：注册短信
     */
    public static void sendSignUp(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, aliYunProperties.getSendSignUp());

    }

    /**
     * 发送：验证码相关
     */
    private static void sendForCode(String phoneNumber, String code, String templateCode) {

        //        String[] templateParamSet =
        //            {code, String.valueOf(BaseConstant.LONG_CODE_EXPIRE_TIME)}; // 备注：第二个元素，表示是：验证码多久过期（分钟）

        String templateParam = "";

        // 执行：发送短信
        doSend(templateCode, templateParam, phoneNumber);

    }

    /**
     * 执行：发送短信
     * 注意：不建议直接调用本方法，而是把本方法，再封装一层再调用
     */
    @SneakyThrows
    public static void doSend(String templateCode, String templateParam, String phoneNumber) {

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(
            Credential.builder().accessKeyId(aliYunProperties.getAccessKeyId())
                .accessKeySecret(aliYunProperties.getAccessKeySecret()).build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder().region("cn-qingdao") // Region ID
            .credentialsProvider(provider)
            .overrideConfiguration(ClientOverrideConfiguration.create().setEndpointOverride("dysmsapi.aliyuncs.com"))
            .build();

        // Parameter settings for API request
        SendSmsRequest sendSmsRequest =
            SendSmsRequest.builder().phoneNumbers(phoneNumber).signName(aliYunProperties.getSignName())
                .templateCode(templateCode).templateParam(templateParam).build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);

        // Synchronously get the return value of the API request
        SendSmsResponse resp = response.get();

        SendSmsResponseBody body = resp.getBody();

        // Finally, close the client
        client.close();

        String code = body.getCode();

        if (BooleanUtil.isFalse("Ok".equals(code))) {
            throw new RuntimeException(StrUtil.format("短信发送失败，code：【{}】，message：【{}】", code, body.getMessage()));
        }

    }

}
