package com.cmcorg20230301.be.engine.sms.base.model.configuration;

import com.cmcorg20230301.be.engine.sms.base.model.bo.SysSmsSendBO;
import com.cmcorg20230301.be.engine.sms.base.model.interfaces.ISysSmsType;

public interface ISysSms {

    /**
     * 短信类型
     */
    ISysSmsType getSysSmsType();

    /**
     * 执行发送
     */
    void send(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：验证码相关
     */
    void sendForCode(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：注册短信
     */
    default void sendSignUp(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSignUp());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：登录短信
     */
    default void sendSignIn(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSignIn());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：设置密码
     */
    default void sendSetPassword(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSetPassword());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改密码
     */
    default void sendUpdatePassword(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdatePassword());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：设置登录名
     */
    default void sendSetSignInName(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSetSignInName());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改登录名
     */
    default void sendUpdateSignInName(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdateSignInName());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：设置邮箱
     */
    default void sendSetEmail(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSetEmail());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改邮箱
     */
    default void sendUpdateEmail(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdateEmail());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：设置微信
     */
    default void sendSetWx(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSetWx());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改微信
     */
    default void sendUpdateWx(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdateWx());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：设置手机
     */
    default void sendSetPhone(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSetPhone());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：修改手机
     */
    default void sendUpdatePhone(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendUpdatePhone());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：设置统一登录
     */
    default void sendSetSingleSignIn(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendForgetPassword());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：忘记密码
     */
    default void sendForgetPassword(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendForgetPassword());

        sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：账号注销
     */
    default void sendSignDelete(SysSmsSendBO sysSmsSendBO) {

        sysSmsSendBO.setTemplateId(sysSmsSendBO.getSysSmsConfigurationDO().getSendSignDelete());

        sendForCode(sysSmsSendBO);

    }

}
