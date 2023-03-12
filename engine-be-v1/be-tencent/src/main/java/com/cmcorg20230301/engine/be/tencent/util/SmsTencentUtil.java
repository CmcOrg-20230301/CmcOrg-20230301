package com.cmcorg20230301.engine.be.tencent.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.tencent.properties.TencentProperties;
import com.tencentcloudapi.common.Credential;
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
            {code, String.valueOf(BaseConstant.LONG_CODE_EXPIRE_TIME)}; // 备注：第二个元素，表示是：验证码多久过期（分钟）

        // 执行：发送短信
        doSend(templateId, templateParamSet, phoneNumber);

    }

    /**
     * 执行：发送短信
     * 注意：不建议直接调用本方法，而是把本方法，再封装一层再调用
     */
    @SneakyThrows
    public static void doSend(String templateId, String[] templateParamSet, String phoneNumber) {

        /* 必要步骤：
         * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
         * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
         * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
         * 以免泄露密钥对危及你的财产安全。
         * SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi */
        Credential credential = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());

        SmsClient smsClient = new SmsClient(credential, "ap-guangzhou");

        SendSmsRequest sendSmsRequest = new SendSmsRequest();

        /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
        // 应用 ID 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看
        sendSmsRequest.setSmsSdkAppId(tencentProperties.getSdkAppId());

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
        // 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
        sendSmsRequest.setSignName(tencentProperties.getSignName());

        /* 模板 ID: 必须填写已审核通过的模板 ID */
        // 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
        sendSmsRequest.setTemplateId(templateId);

        /* 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
        sendSmsRequest.setTemplateParamSet(templateParamSet);

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+86" + phoneNumber; // 默认增加： +86
        }

        sendSmsRequest.setPhoneNumberSet(new String[] {phoneNumber});

        /* 用户的 session 内容（无需要可忽略）: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "";
        sendSmsRequest.setSessionContext(sessionContext);

        /* 短信码号扩展号（无需要可忽略）: 默认未开通，如需开通请联系 [腾讯云短信小助手] */
        String extendCode = "";
        sendSmsRequest.setExtendCode(extendCode);

        /* 国际/港澳台短信 SenderId（无需要可忽略）: 国内短信填空，默认未开通，如需开通请联系 [腾讯云短信小助手] */
        String senderid = "";
        sendSmsRequest.setSenderId(senderid);

        /* 通过 smsClient 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 sendSmsResponse 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse sendSmsResponse = smsClient.SendSms(sendSmsRequest);

        if (sendSmsResponse.getSendStatusSet().length == 0) {
            throw new RuntimeException(StrUtil.format("短信发送失败，请联系管理员"));
        }

        SendStatus sendStatus = sendSmsResponse.getSendStatusSet()[0];

        String code = sendStatus.getCode();

        if (BooleanUtil.isFalse("Ok".equals(code))) {
            throw new RuntimeException(StrUtil.format("短信发送失败，code：【{}】，message：【{}】", code, sendStatus.getMessage()));
        }

    }

}
