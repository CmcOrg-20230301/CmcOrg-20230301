package com.cmcorg20230301.engine.be.tencent.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.tencent.properties.TencentProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * 腾讯：短信工具类
 */
@Component
public class SmsTencentUtil {

    public static TencentProperties tencentProperties;

    public SmsTencentUtil(TencentProperties tencentProperties) {

        SmsTencentUtil.tencentProperties = tencentProperties;

    }

    /**
     * 发送：账号注销
     */
    public static void sendDelete(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendDelete());

    }

    /**
     * 发送：绑定手机
     */
    public static void sendBind(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendBind());

    }

    /**
     * 发送：修改手机
     */
    public static void sendUpdate(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendUpdate());

    }

    /**
     * 发送：修改密码
     */
    public static void sendUpdatePassword(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendUpdatePassword());

    }

    /**
     * 发送：忘记密码
     */
    public static void sendForgetPassword(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendForgetPassword());

    }

    /**
     * 发送：登录短信
     */
    public static void sendSignIn(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendSignIn());

    }

    /**
     * 发送：注册短信
     */
    public static void sendSignUp(String phoneNumber, String code) {

        sendForCode(phoneNumber, code, tencentProperties.getSendSignUp());

    }

    /**
     * 发送：验证码相关
     */
    private static void sendForCode(String phoneNumber, String code, String templateId) {

        String[] templateParamSet =
            {code, String.valueOf(BaseConstant.LONG_CODE_EXPIRE_MINUTE)}; // 备注：第二个元素，表示是：验证码多久过期（分钟）

        // 执行：发送短信
        doSend(templateId, templateParamSet, phoneNumber);

    }

    /**
     * 执行：发送短信
     * 注意：不建议直接调用本方法，而是把本方法，再封装一层再调用
     */
    @SneakyThrows
    public static void doSend(String templateId, String[] templateParamSet, String phoneNumber) {

        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());

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

        req.setSmsSdkAppId(tencentProperties.getSdkAppId());

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
        // 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
        req.setSignName(tencentProperties.getSignName());

        /* 模板 ID: 必须填写已审核通过的模板 ID */
        // 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
        req.setTemplateId(templateId);

        /* 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
        req.setTemplateParamSet(templateParamSet);

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        req.setPhoneNumberSet(new String[] {phoneNumber});

        // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
        SendSmsResponse resp = client.SendSms(req);

        if (resp.getSendStatusSet().length == 0) {
            throw new RuntimeException(StrUtil.format("短信发送失败，请联系管理员"));
        }

        SendStatus sendStatus = resp.getSendStatusSet()[0];

        String code = sendStatus.getCode();

        if (BooleanUtil.isFalse("Ok".equals(code))) {
            throw new RuntimeException(StrUtil.format("短信发送失败，code：【{}】，message：【{}】", code, sendStatus.getMessage()));
        }

    }

}
