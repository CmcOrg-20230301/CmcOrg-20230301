package com.cmcorg20230301.be.engine.sign.signinname.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.util.MyEmailUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserConfigurationDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.service.SysUserConfigurationService;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;
import com.cmcorg20230301.be.engine.sign.signinname.service.SignSignInNameService;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsHelper;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsUtil;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignSignInNameServiceImpl implements SignSignInNameService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_SIGN_IN_NAME;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserConfigurationService sysUserConfigurationService;

    @Resource
    RedissonClient redissonClient;

    /**
     * 注册
     */
    @Override
    public String signUp(SignSignInNameSignUpDTO dto) {

        SysUserConfigurationDO sysUserConfigurationDO =
                sysUserConfigurationService.getSysUserConfigurationDoByTenantId(dto.getTenantId());

        if (BooleanUtil.isFalse(sysUserConfigurationDO.getSignInNameSignUpEnable())) {
            ApiResultVO.errorMsg("操作失败：不允许用户名注册，请联系管理员");
        }

        return SignUtil
                .signUp(dto.getPassword(), dto.getOriginPassword(), null, BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(),
                        dto.getTenantId());

    }

    /**
     * 账号密码登录
     */
    @Override
    public SignInVO signInPassword(SignSignInNameSignInPasswordDTO dto) {

        return SignUtil.signInPassword(
                ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()),
                dto.getPassword(), dto.getSignInName(), dto.getTenantId());

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignSignInNameUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), BaseRedisKeyEnum.PRE_SIGN_IN_NAME, null,
                dto.getOldPassword());

    }

    /**
     * 修改登录名
     */
    @Override
    public String updateSignInName(SignSignInNameUpdateSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.updateAccount(null, null, BaseRedisKeyEnum.PRE_SIGN_IN_NAME, BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getNewSignInName(), dto.getCurrentPassword(), null);

    }

    /**
     * 设置邮箱：发送验证码
     */
    @Override
    public String setEmailSendCode(SignSignInNameSetEmailSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 设置邮箱
     */
    @Override
    public String setEmail(SignSignInNameSetEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_EMAIL, dto.getEmail(), null, null, dto.getCurrentPassword());

    }

    /**
     * 设置微信：获取二维码地址
     */
    @Override
    public GetQrCodeVO setWxGetQrCodeUrl() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, SysQrCodeSceneTypeEnum.WX_BIND);

    }

    /**
     * 设置微信：获取二维码是否已经被扫描
     */
    @Override
    public SysQrCodeSceneBindVO getQrCodeSceneFlag(NotNullId notNullId) {

        // 执行
        return SignUtil.getSysQrCodeSceneBindVoAndHandle(notNullId.getId(), false, null);

    }

    /**
     * 设置微信
     */
    @Override
    public SysQrCodeSceneBindVO setWx(SignSignInNameSetWxDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.setWx(dto.getId(), null, null, dto.getCurrentPassword());

    }

    /**
     * 设置手机：发送验证码
     */
    @Override
    public String setPhoneSendCode(SignSignInNameSetPhoneSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                        BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SysSmsUtil
                                .sendSetPhone(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, dto.getPhone())), currentTenantIdDefault);

    }

    /**
     * 设置手机
     */
    @Override
    public String setPhone(SignSignInNameSetPhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_PHONE, dto.getPhone(), null, null, dto.getCurrentPassword());

    }

    /**
     * 设置统一登录：获取统一登录微信的二维码地址
     */
    @Override
    public GetQrCodeVO setSingleSignInGetQrCodeUrlSingleSignIn() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWxForSingleSignIn(true, SysQrCodeSceneTypeEnum.WX_SINGLE_SIGN_IN_BIND);

    }

    /**
     * 设置统一登录：获取统一登录微信的二维码是否已经被扫描
     */
    @Override
    public SysQrCodeSceneBindVO setSingleSignInGetQrCodeSceneFlagSingleSignIn(NotNullId notNullId) {

        // 执行
        return SignUtil.getSysQrCodeSceneBindVoAndHandleForSingleSignIn(notNullId.getId(), false, null);

    }

    /**
     * 设置统一登录
     */
    @Override
    public SysQrCodeSceneBindVO setSingleSignIn(SignSignInNameSetSingleSignInDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.setWxForSingleSignIn(dto.getId(), null, null, dto.getCurrentPassword());

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(SignSignInNameSignDeleteDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.signDelete(null, BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getCurrentPassword(), null);

    }

}
