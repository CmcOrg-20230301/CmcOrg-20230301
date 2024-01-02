package com.cmcorg20230301.be.engine.sms.tencent.configuration;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.sms.base.model.bo.SysSmsSendBO;
import com.cmcorg20230301.be.engine.sms.base.model.configuration.ISysSms;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.model.enums.SysSmsTypeEnum;
import com.cmcorg20230301.be.engine.sms.base.model.interfaces.ISysSmsType;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云短信相关配置类
 */
@Configuration
public class SmsTencentConfiguration implements ISysSms {

    @Override
    public ISysSmsType getSysSmsType() {
        return SysSmsTypeEnum.TENCENT_YUN;
    }

    /**
     * 发送：账号注销
     */
    @Override
    public void sendDelete(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendDelete());

        // 执行
        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：绑定手机
     */
    @Override
    public void sendBind(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendBind());

        // 执行
        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改手机
     */
    @Override
    public void sendUpdate(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdate());

        // 执行
        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改密码
     */
    @Override
    public void sendUpdatePassword(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdatePassword());

        // 执行
        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：忘记密码
     */
    @Override
    public void sendForgetPassword(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendForgetPassword());

        // 执行
        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：登录短信
     */
    @Override
    public void sendSignIn(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSignIn());

        // 执行
        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：注册短信
     */
    @Override
    public void sendSignUp(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSignUp());

        // 执行
        sendForCode(sysSmsSendBO);

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
    public static void sendForCode(SysSmsSendBO sysSmsSendBO) {

        if (StrUtil.isBlank(sysSmsSendBO.getTemplateId())) {

            sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendCommon());

        }

        String[] templateParamSet = {sysSmsSendBO.getSendContent(),
            String.valueOf(BaseConstant.LONG_CODE_EXPIRE_MINUTE)}; // 备注：第二个元素，表示是：验证码多久过期（分钟）

        sysSmsSendBO.setTemplateParamSet(templateParamSet);

        // 执行：发送短信
        doSend(sysSmsSendBO);

    }

    /**
     * 执行：发送
     */
    @SneakyThrows
    public static void doSend(SysSmsSendBO sysSmsSendBO) {

        SysSmsConfigurationDO sysSmsConfigurationDO = sysSmsSendBO.getSysSmsConfigurationDO();

        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(sysSmsConfigurationDO.getSecretId(), sysSmsConfigurationDO.getSecretKey());

        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        // 实例化要请求产品的client对象,clientProfile是可选的
        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);

        // 实例化一个请求对象,每个接口都会对应一个request对象
        SendSmsRequest req = new SendSmsRequest();

        req.setSmsSdkAppId(sysSmsConfigurationDO.getSdkAppId());

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
        // 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
        req.setSignName(sysSmsConfigurationDO.getSignName());

        /* 模板 ID: 必须填写已审核通过的模板 ID */
        // 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
        req.setTemplateId(sysSmsSendBO.getTemplateId());

        /* 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
        req.setTemplateParamSet(sysSmsSendBO.getTemplateParamSet());

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        req.setPhoneNumberSet(new String[] {sysSmsSendBO.getPhoneNumber()});

        // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
        SendSmsResponse resp = client.SendSms(req);

        if (resp.getSendStatusSet().length == 0) {
            throw new RuntimeException(StrUtil.format("腾讯云短信发送失败，请联系管理员"));
        }

        SendStatus sendStatus = resp.getSendStatusSet()[0];

        String code = sendStatus.getCode();

        if (BooleanUtil.isFalse("Ok".equalsIgnoreCase(code))) {
            throw new RuntimeException(
                StrUtil.format("腾讯云短信发送失败，code：【{}】，message：【{}】", code, sendStatus.getMessage()));
        }

    }

}
