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
     * 发送：账号注销
     */
    void sendDelete(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：绑定手机
     */
    void sendBind(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：修改手机
     */
    void sendUpdate(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：修改密码
     */
    void sendUpdatePassword(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：忘记密码
     */
    void sendForgetPassword(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：登录短信
     */
    void sendSignIn(SysSmsSendBO sysSmsSendBO);

    /**
     * 发送：注册短信
     */
    void sendSignUp(SysSmsSendBO sysSmsSendBO);

}
