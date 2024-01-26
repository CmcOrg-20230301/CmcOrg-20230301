package com.cmcorg20230301.be.engine.sms.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sms.base.model.bo.SysSmsSendBO;
import com.cmcorg20230301.be.engine.sms.base.model.configuration.ISysSms;
import org.jetbrains.annotations.NotNull;
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
     * 执行发送
     */
    public static void send(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.send(sysSmsSendBO);

    }

    /**
     * 发送：验证码相关
     */
    public static void sendForCode(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendForCode(sysSmsSendBO);

    }

    /**
     * 发送：注册短信
     */
    public static void sendSignUp(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSignUp(sysSmsSendBO);

    }

    /**
     * 发送：登录短信
     */
    public static void sendSignIn(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSignIn(sysSmsSendBO);

    }

    /**
     * 发送：设置密码
     */
    public static void sendSetPassword(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSetPassword(sysSmsSendBO);

    }

    /**
     * 发送：修改密码
     */
    public static void sendUpdatePassword(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendUpdatePassword(sysSmsSendBO);

    }

    /**
     * 发送：设置登录名
     */
    public static void sendSetSignInName(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSetSignInName(sysSmsSendBO);

    }

    /**
     * 发送：修改登录名
     */
    public static void sendUpdateSignInName(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendUpdateSignInName(sysSmsSendBO);

    }

    /**
     * 发送：设置邮箱
     */
    public static void sendSetEmail(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSetEmail(sysSmsSendBO);

    }

    /**
     * 发送：修改邮箱
     */
    public static void sendUpdateEmail(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendUpdateEmail(sysSmsSendBO);

    }

    /**
     * 发送：设置微信
     */
    public static void sendSetWx(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSetWx(sysSmsSendBO);

    }

    /**
     * 发送：修改微信
     */
    public static void sendUpdateWx(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendUpdateWx(sysSmsSendBO);

    }

    /**
     * 发送：设置手机
     */
    public static void sendSetPhone(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSetPhone(sysSmsSendBO);

    }

    /**
     * 发送：修改手机
     */
    public static void sendUpdatePhone(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendUpdatePhone(sysSmsSendBO);

    }

    /**
     * 发送：设置统一登录
     */
    public static void sendSetSingleSignIn(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSetSingleSignIn(sysSmsSendBO);

    }

    /**
     * 发送：忘记密码
     */
    public static void sendForgetPassword(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendForgetPassword(sysSmsSendBO);

    }

    /**
     * 发送：账号注销
     */
    public static void sendSignDelete(SysSmsSendBO sysSmsSendBO) {

        ISysSms isysSms = getIsysSms(sysSmsSendBO);

        // 执行
        isysSms.sendSignDelete(sysSmsSendBO);

    }

    /**
     * 获取：短信实现类
     */
    @NotNull
    public static ISysSms getIsysSms(SysSmsSendBO sysSmsSendBO) {

        if (StrUtil.isBlank(sysSmsSendBO.getPhoneNumber())) {

            ApiResultVO.error(BaseBizCodeEnum.THERE_IS_NO_BOUND_MOBILE_PHONE_NUMBER_SO_THIS_OPERATION_CANNOT_BE_PERFORMED);

        }

        SysSmsHelper.handleSysSmsConfigurationDO(sysSmsSendBO);

        // 执行：获取
        return doGetIsysSms(sysSmsSendBO);

    }

    /**
     * 获取：短信实现类
     */
    @NotNull
    public static ISysSms doGetIsysSms(SysSmsSendBO sysSmsSendBO) {

        if (StrUtil.isBlank(sysSmsSendBO.getSendContent())) {
            ApiResultVO.errorMsg("操作失败：发送内容不能为空");
        }

        Integer smsType = sysSmsSendBO.getSysSmsConfigurationDO().getType();

        ISysSms iSysSms = SYS_SMS_MAP.get(smsType);

        if (iSysSms == null) {
            ApiResultVO.errorMsg("操作失败：短信方式未找到：{}", smsType);
        }

        return iSysSms;

    }

}
