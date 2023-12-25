package com.cmcorg20230301.be.engine.sms.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sms.base.model.SysSmsSendBO;
import com.cmcorg20230301.be.engine.sms.base.model.configuration.ISysSms;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 短信工具类
 */
@Component
public class SysSmsUtil {

    private static final Map<Integer, ISysSms> SYS_SMS_MAP = MapUtil.newHashMap();

    public SysSmsUtil(@Autowired(required = false) @Nullable List<ISysSms> iSysSmsList) {

        if (CollUtil.isNotEmpty(iSysSmsList)) {

            for (ISysSms item : iSysSmsList) {

                SYS_SMS_MAP.put(item.getSysSmsType().getCode(), item);

            }

        }

    }

    /**
     * 发送：账号注销
     */
    public static void sendDelete(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendDelete(sysSmsSendBO);

    }

    /**
     * 发送：绑定手机
     */
    public static void sendBind(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendBind(sysSmsSendBO);

    }

    /**
     * 发送：修改手机
     */
    public static void sendUpdate(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendUpdate(sysSmsSendBO);

    }

    /**
     * 发送：修改密码
     */
    public static void sendUpdatePassword(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendUpdatePassword(sysSmsSendBO);

    }

    /**
     * 发送：忘记密码
     */
    public static void sendForgetPassword(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendForgetPassword(sysSmsSendBO);

    }

    /**
     * 发送：登录短信
     */
    public static void sendSignIn(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendSignIn(sysSmsSendBO);

    }

    /**
     * 发送：注册短信
     */
    public static void sendSignUp(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.sendSignUp(sysSmsSendBO);

    }

    /**
     * 执行发s短信
     */
    public static void send(SysSmsSendBO sysSmsSendBO) {

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        ISysSms iSysSms = getIsysSms(sysSmsSendBO);

        if (iSysSms == null) {
            return;
        }

        // 执行：发送
        iSysSms.send(sysSmsSendBO);

    }

    /**
     * 获取：短信实现类
     */
    @Nullable
    public static ISysSms getIsysSms(SysSmsSendBO sysSmsSendBO) {

        if (StrUtil.isBlank(sysSmsSendBO.getSendContent())) {
            return null;
        }

        Integer smsType = sysSmsSendBO.getSysSmsConfigurationDO().getType();

        ISysSms iSysSms = SYS_SMS_MAP.get(smsType);

        if (iSysSms == null) {
            ApiResultVO.errorMsg("操作失败：短信方式未找到：{}", smsType);
        }

        return iSysSms;

    }

}
