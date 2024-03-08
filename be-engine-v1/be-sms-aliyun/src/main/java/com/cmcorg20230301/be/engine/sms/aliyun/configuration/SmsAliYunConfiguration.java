package com.cmcorg20230301.be.engine.sms.aliyun.configuration;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Configuration;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.sms.base.model.bo.SysSmsSendBO;
import com.cmcorg20230301.be.engine.sms.base.model.configuration.ISysSms;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.model.enums.SysSmsTypeEnum;
import com.cmcorg20230301.be.engine.sms.base.model.interfaces.ISysSmsType;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.SneakyThrows;

/**
 * 阿里云短信相关配置类
 */
@Configuration
public class SmsAliYunConfiguration implements ISysSms {

    @Override
    public ISysSmsType getSysSmsType() {
        return SysSmsTypeEnum.ALI_YUN;
    }

    /**
     * 执行发送
     */
    @Override
    public void send(SysSmsSendBO sysSmsSendBO) {

        // 执行
        doSend(sysSmsSendBO);

    }

    /**
     * 发送：验证码相关
     */
    public void sendForCode(SysSmsSendBO sysSmsSendBO) {

        if (StrUtil.isBlank(sysSmsSendBO.getTemplateId())) {

            sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendCommon());

        }

        // 备注：第二个元素，表示是：验证码多久过期（分钟）
        String templateParam = JSONUtil.createObj().set("code", sysSmsSendBO.getSendContent())
            .set("expire", BaseConstant.LONG_CODE_EXPIRE_MINUTE).toString();

        sysSmsSendBO.setTemplateParamSet(new String[] {templateParam});

        // 执行：发送短信
        doSend(sysSmsSendBO);

    }

    /**
     * 执行：发送
     */
    @SneakyThrows
    public static void doSend(SysSmsSendBO sysSmsSendBO) {

        SysSmsConfigurationDO sysSmsConfigurationDO = sysSmsSendBO.getSysSmsConfigurationDO();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder().phoneNumbers(sysSmsSendBO.getPhoneNumber())
            .signName(sysSmsConfigurationDO.getSignName()).templateCode(sysSmsSendBO.getTemplateId())
            .templateParam(sysSmsSendBO.getTemplateParamSet()[0]).build();

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider =
            StaticCredentialProvider.create(Credential.builder().accessKeyId(sysSmsConfigurationDO.getSecretId())
                .accessKeySecret(sysSmsConfigurationDO.getSecretKey()).build());

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

            throw new RuntimeException(StrUtil.format("阿里云短信发送失败，code：【{}】，message：【{}】", code, body.getMessage()));

        }

    }

}
