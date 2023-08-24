package com.cmcorg20230301.engine.be.sms.base.util;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.sms.aliyun.util.SmsAliYunUtil;
import com.cmcorg20230301.engine.be.sms.base.properties.SmsProperties;
import com.cmcorg20230301.engine.be.sms.tencent.util.SmsTencentUtil;
import org.springframework.stereotype.Component;

/**
 * 短信工具类
 */
@Component
public class SmsUtil {

    public static SmsProperties smsProperties;

    public SmsUtil(SmsProperties smsProperties) {

        SmsUtil.smsProperties = smsProperties;

    }

    public static void typeError() {

        throw new RuntimeException(StrUtil.format("短信发送失败：发送类型配置错误，请联系管理员"));

    }

    /**
     * 发送：账号注销
     */
    public static void sendDelete(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendDelete(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendDelete(phoneNumber, code);

        } else {

            typeError();

        }

    }

    /**
     * 发送：绑定手机
     */
    public static void sendBind(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendBind(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendBind(phoneNumber, code);

        } else {

            typeError();

        }

    }

    /**
     * 发送：修改手机
     */
    public static void sendUpdate(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendUpdate(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendUpdate(phoneNumber, code);

        } else {

            typeError();

        }

    }

    /**
     * 发送：修改密码
     */
    public static void sendUpdatePassword(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendUpdatePassword(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendUpdatePassword(phoneNumber, code);

        } else {

            typeError();

        }

    }

    /**
     * 发送：忘记密码
     */
    public static void sendForgetPassword(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendForgetPassword(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendForgetPassword(phoneNumber, code);

        } else {

            typeError();

        }

    }

    /**
     * 发送：登录短信
     */
    public static void sendSignIn(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendSignIn(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendSignIn(phoneNumber, code);

        } else {

            typeError();

        }

    }

    /**
     * 发送：注册短信
     */
    public static void sendSignUp(String phoneNumber, String code) {

        if (smsProperties.getType() == 1) { // 1 腾讯云

            SmsTencentUtil.sendSignUp(phoneNumber, code);

        } else if (smsProperties.getType() == 2) { // 2 阿里云

            SmsAliYunUtil.sendSignUp(phoneNumber, code);

        } else {

            typeError();

        }

    }

}
