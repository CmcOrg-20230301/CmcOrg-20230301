package com.cmcorg20230301.be.engine.sign.email.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.util.MyEmailUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserConfigurationDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SingleSignInProperties;
import com.cmcorg20230301.be.engine.security.service.SysUserConfigurationService;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;
import com.cmcorg20230301.be.engine.sign.email.service.SignEmailService;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsHelper;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignEmailServiceImpl implements SignEmailService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_EMAIL;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserConfigurationService sysUserConfigurationService;

    @Resource
    SingleSignInProperties singleSignInProperties;

    /**
     * 注册-发送验证码
     */
    @Override
    public String signUpSendCode(EmailNotBlankDTO dto) {

        checkSignUpEnable(dto.getTenantId()); // 检查：是否允许注册

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED,
                        (code) -> MyEmailUtil.send(dto.getEmail(), EmailMessageEnum.SIGN_UP, code, dto.getTenantId()), dto.getTenantId());

    }

    /**
     * 检查：是否允许注册
     */
    private void checkSignUpEnable(Long tenantId) {

        SysUserConfigurationDO sysUserConfigurationDO =
                sysUserConfigurationService.getSysUserConfigurationDoByTenantId(tenantId);

        if (BooleanUtil.isFalse(sysUserConfigurationDO.getEmailSignUpEnable())) {
            ApiResultVO.errorMsg("操作失败：不允许邮箱注册，请联系管理员");
        }

    }

    /**
     * 注册
     */
    @Override
    public String signUp(SignEmailSignUpDTO dto) {

        checkSignUpEnable(dto.getTenantId()); // 检查：是否允许注册

        return SignUtil
                .signUp(dto.getPassword(), dto.getOriginPassword(), dto.getCode(), BaseRedisKeyEnum.PRE_EMAIL, dto.getEmail(),
                        dto.getTenantId());

    }

    /**
     * 邮箱：账号密码登录
     */
    @Override
    public SignInVO signInPassword(SignEmailSignInPasswordDTO dto) {

        return SignUtil
                .signInPassword(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()),
                        dto.getPassword(), dto.getEmail(), dto.getTenantId());

    }

    /**
     * 修改密码-发送验证码
     */
    @Override
    public String updatePasswordSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_EMAIL, (code, account) -> MyEmailUtil
                .send(account, EmailMessageEnum.UPDATE_PASSWORD, code, currentTenantIdDefault));

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignEmailUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), BaseRedisKeyEnum.PRE_EMAIL, dto.getCode(), null);

    }

    /**
     * 设置登录名-发送验证码
     */
    @Override
    public String setSignInNameSendCode(SignEmailSetSignInNameSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_SIGN_IN_NAME + dto.getSignInName();

        return SignUtil.sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()), false,
                BizCodeEnum.SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.SET_SIGN_IN_NAME, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 设置登录名
     */
    @Override
    public String setSignInName(SignEmailSetSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(), null, null, null);

    }

    /**
     * 修改登录名-发送验证码
     */
    @Override
    public String updateSignInNameSendCode(SignEmailUpdateSignInNameSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_SIGN_IN_NAME + dto.getSignInName();

        return SignUtil.sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()), false,
                BizCodeEnum.SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.UPDATE_SIGN_IN_NAME, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 修改登录名
     */
    @Override
    public String updateSignInName(SignEmailUpdateSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(), null, null, null);

    }

    /**
     * 修改邮箱-发送新邮箱验证码
     */
    @Override
    public String updateEmailSendCodeNew(SignEmailUpdateEmailSendCodeNewDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 修改邮箱-发送旧邮箱验证码
     */
    @Override
    public String updateEmailSendCodeOld() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        return SignUtil.sendCode(key, null, true,
                BaseBizCodeEnum.API_RESULT_SYS_ERROR,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.UPDATE_EMAIL, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 修改邮箱
     */
    @Override
    public String updateEmail(SignEmailUpdateEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updateAccount(dto.getOldEmailCode(), dto.getNewEmailCode(), BaseRedisKeyEnum.PRE_EMAIL, BaseRedisKeyEnum.PRE_EMAIL, dto.getNewEmail(), null, null);

    }

    /**
     * 设置微信：发送验证码
     */
    @Override
    public String setWxSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        return SignUtil.sendCode(key, null, true,
                BaseBizCodeEnum.API_RESULT_SYS_ERROR,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.BIND_WX, code, currentTenantIdDefault), currentTenantIdDefault);

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
    public SysQrCodeSceneBindVO setWx(SignEmailSetWxDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String codeKey = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        // 执行
        return SignUtil.setWx(dto.getQrCodeId(), dto.getEmailCode(), codeKey, null);

    }

    /**
     * 设置手机：发送邮箱验证码
     */
    @Override
    public String setPhoneSendCodeEmail() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        return SignUtil.sendCode(key, null, true,
                BaseBizCodeEnum.API_RESULT_SYS_ERROR,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.BIND_PHONE, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 设置手机：发送手机验证码
     */
    @Override
    public String setPhoneSendCodePhone(SignEmailSetPhoneSendCodePhoneDTO dto) {

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
    public String setPhone(SignEmailSetPhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updateAccount(dto.getEmailCode(), dto.getPhoneCode(), BaseRedisKeyEnum.PRE_EMAIL, BaseRedisKeyEnum.PRE_PHONE, dto.getPhone(), null, null);

    }

    /**
     * 设置统一登录：微信：发送邮箱验证码
     */
    @Override
    public String setSingleSignInWxSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        return SignUtil.sendCode(key, null, true,
                BaseBizCodeEnum.API_RESULT_SYS_ERROR,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.SET_SINGLE_SIGN_IN, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 设置统一登录：微信：获取统一登录微信的二维码地址
     */
    @Override
    public GetQrCodeVO setSingleSignInWxGetQrCodeUrl() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWxForSingleSignIn(true, SysQrCodeSceneTypeEnum.WX_SINGLE_SIGN_IN_BIND);

    }

    /**
     * 设置统一登录：微信：获取统一登录微信的二维码是否已经被扫描
     */
    @Override
    public SysQrCodeSceneBindVO setSingleSignInWxGetQrCodeSceneFlag(NotNullId notNullId) {

        // 执行
        return SignUtil.getSysQrCodeSceneBindVoAndHandleForSingleSignIn(notNullId.getId(), false, null);

    }

    /**
     * 设置统一登录：微信
     */
    @Override
    public SysQrCodeSceneBindVO setSingleSignInWx(SignEmailSetSingleSignInWxDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String codeKey = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        // 执行
        return SignUtil.setWxForSingleSignIn(dto.getQrCodeId(), dto.getEmailCode(), codeKey, null);

    }

    /**
     * 设置统一登录：手机验证码：发送当前账号已经绑定邮箱的验证码
     */
    @Override
    public String setSingleSignInPhoneSendCodeCurrent() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = BaseRedisKeyEnum.PRE_EMAIL + currentUserEmailNotAdmin;

        return SignUtil.sendCode(key, null, true,
                BaseBizCodeEnum.API_RESULT_SYS_ERROR,
                (code) -> MyEmailUtil
                        .send(currentUserEmailNotAdmin, EmailMessageEnum.SET_SINGLE_SIGN_IN, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 设置统一登录：手机验证码：发送要绑定统一登录手机的验证码
     */
    @Override
    public String setSingleSignInSendCodePhone(SignEmailSetSingleSignInPhoneSendCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.sendCodeForSingle(dto.getPhone(), false, "操作失败：该手机号已被绑定", (code) -> SysSmsUtil
                .sendSignIn(SysSmsHelper.getSysSmsSendBO(code, dto.getPhone(), singleSignInProperties.getSmsConfigurationId())), BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE);

    }

    /**
     * 设置统一登录：手机验证码
     */
    @Override
    public String setSingleSignInPhone(SignEmailSetSingleSignInPhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.bindAccountForSingle(dto.getSingleSignInPhoneCode(), BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE, dto.getSingleSignInPhone(), null, dto.getCurrentEmailCode(), BaseRedisKeyEnum.PRE_EMAIL);

    }

    /**
     * 忘记密码-发送验证码
     */
    @Override
    public String forgetPasswordSendCode(EmailNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getEmail(), dto.getTenantId(), null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), true,
                        com.cmcorg20230301.be.engine.email.exception.BizCodeEnum.EMAIL_NOT_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.FORGET_PASSWORD, code, dto.getTenantId()), dto.getTenantId());

    }

    /**
     * 忘记密码
     */
    @Override
    public String forgetPassword(SignEmailForgetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getEmail(), dto.getTenantId(), null); // 检查：是否可以进行操作

        return SignUtil
                .forgetPassword(dto.getNewPassword(), dto.getOriginNewPassword(), dto.getCode(), BaseRedisKeyEnum.PRE_EMAIL,
                        dto.getEmail(), ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), dto.getTenantId());

    }

    /**
     * 账号注销-发送验证码
     */
    @Override
    public String signDeleteSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_EMAIL, (code, account) -> MyEmailUtil
                .send(account, EmailMessageEnum.SIGN_DELETE, code, currentTenantIdDefault));

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(NotBlankCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.signDelete(dto.getCode(), BaseRedisKeyEnum.PRE_EMAIL, null, null);

    }

}
