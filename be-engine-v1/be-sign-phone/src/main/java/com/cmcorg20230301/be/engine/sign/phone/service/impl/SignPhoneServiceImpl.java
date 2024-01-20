package com.cmcorg20230301.be.engine.sign.phone.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.util.MyEmailUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
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
import com.cmcorg20230301.be.engine.security.service.SysUserConfigurationService;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.phone.model.dto.*;
import com.cmcorg20230301.be.engine.sign.phone.service.SignPhoneService;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsHelper;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignPhoneServiceImpl implements SignPhoneService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_PHONE;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserConfigurationService sysUserConfigurationService;

    /**
     * 注册-发送验证码
     */
    @Override
    public String signUpSendCode(PhoneNotBlankDTO dto) {

        checkSignUpEnable(dto.getTenantId()); // 检查：是否允许注册

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                        BizCodeEnum.PHONE_HAS_BEEN_REGISTERED,
                        (code) -> SysSmsUtil.sendSignUp(SysSmsHelper.getSysSmsSendBO(dto.getTenantId(), code, dto.getPhone())), dto.getTenantId());

    }

    /**
     * 检查：是否允许注册
     */
    private void checkSignUpEnable(Long tenantId) {

        SysUserConfigurationDO sysUserConfigurationDO =
                sysUserConfigurationService.getSysUserConfigurationDoByTenantId(tenantId);

        if (BooleanUtil.isFalse(sysUserConfigurationDO.getPhoneSignUpEnable())) {
            ApiResultVO.errorMsg("操作失败：不允许手机号码注册，请联系管理员");
        }

    }

    /**
     * 注册
     */
    @Override
    public String signUp(SignPhoneSignUpDTO dto) {

        checkSignUpEnable(dto.getTenantId()); // 检查：是否允许注册

        return SignUtil
                .signUp(dto.getPassword(), dto.getOriginPassword(), dto.getCode(), BaseRedisKeyEnum.PRE_PHONE, dto.getPhone(),
                        dto.getTenantId());

    }

    /**
     * 手机：账号密码登录
     */
    @Override
    public SignInVO signInPassword(SignPhoneSignInPasswordDTO dto) {

        return SignUtil
                .signInPassword(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                        dto.getPassword(), dto.getPhone(), dto.getTenantId());

    }

    /**
     * 手机验证码登录-发送验证码
     */
    @Override
    public String signInSendCode(PhoneNotBlankDTO dto) {

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), null,
                        BaseBizCodeEnum.API_RESULT_SYS_ERROR, (code) -> SysSmsUtil
                                .sendSignIn(SysSmsHelper.getSysSmsSendBO(dto.getTenantId(), code, dto.getPhone())), dto.getTenantId());

    }

    /**
     * 手机验证码登录
     */
    @Override
    public SignInVO signInCode(SignPhoneSignInCodeDTO dto) {

        return SignUtil
                .signInCode(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                        dto.getCode(), BaseRedisKeyEnum.PRE_PHONE, dto.getPhone(), dto.getTenantId(), null);

    }

    /**
     * 设置密码-发送验证码
     */
    @Override
    public String setPasswordSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendSetPassword(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 设置密码
     */
    @Override
    public String setPassword(SignPhoneSetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 修改密码
        return SignUtil
                .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), BaseRedisKeyEnum.PRE_PHONE, dto.getCode(), null);

    }

    /**
     * 修改密码-发送验证码
     */
    @Override
    public String updatePasswordSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendUpdatePassword(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignPhoneUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), BaseRedisKeyEnum.PRE_PHONE, dto.getCode(), null);

    }

    /**
     * 设置登录名-发送验证码
     */
    @Override
    public String setSignInNameSendCode(SignPhoneSetSignInNameSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String key = BaseRedisKeyEnum.PRE_SIGN_IN_NAME + dto.getSignInName();

        return SignUtil.sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()), false,
                BizCodeEnum.SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER, code -> SysSmsUtil
                        .sendSetSignInName(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, currentUserPhoneNotAdmin)), currentTenantIdDefault);

    }

    /**
     * 设置登录名
     */
    @Override
    public String setSignInName(SignPhoneSetSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 设置登录名
        return SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(), null, null, null);

    }

    /**
     * 修改登录名-发送验证码
     */
    @Override
    public String updateSignInNameSendCode(SignPhoneUpdateSignInNameSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String key = BaseRedisKeyEnum.PRE_SIGN_IN_NAME + dto.getSignInName();

        return SignUtil.sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()), false,
                BizCodeEnum.SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER, code -> SysSmsUtil
                        .sendUpdateSignInName(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, currentUserPhoneNotAdmin)), currentTenantIdDefault);

    }

    /**
     * 修改登录名
     */
    @Override
    public String updateSignInName(SignPhoneUpdateSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 设置登录名
        return SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(), null, null, null);

    }

    /**
     * 设置邮箱-发送手机验证码
     */
    @Override
    public String setEmailSendCodePhone(SignPhoneSetEmailSendCodePhoneDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：邮箱是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false, BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, currentTenantIdDefault);

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendSetEmail(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 设置邮箱-发送邮箱验证码
     */
    @Override
    public String setEmailSendCodeEmail(SignPhoneSetEmailSendCodeEmailDTO dto) {

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
    public String setEmail(SignPhoneSetEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updateAccount(dto.getPhoneCode(), dto.getEmailCode(), BaseRedisKeyEnum.PRE_PHONE, BaseRedisKeyEnum.PRE_EMAIL, dto.getEmail(), null, null);

    }

    /**
     * 修改邮箱-发送手机验证码
     */
    @Override
    public String updateEmailSendCodePhone(SignPhoneUpdateEmailSendCodePhoneDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：邮箱是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false, BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, currentTenantIdDefault);

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendUpdateEmail(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 修改邮箱-发送邮箱验证码
     */
    @Override
    public String updateEmailSendCodeEmail(SignPhoneUpdateEmailSendCodeEmailDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 修改邮箱
     */
    @Override
    public String updateEmail(SignPhoneUpdateEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updateAccount(dto.getPhoneCode(), dto.getEmailCode(), BaseRedisKeyEnum.PRE_PHONE, BaseRedisKeyEnum.PRE_EMAIL, dto.getEmail(), null, null);

    }

    /**
     * 设置微信-发送手机验证码
     */
    @Override
    public String setWxSendCodePhone() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendSetWx(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

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
     * 设置微信
     */
    @Override
    public SysQrCodeSceneBindVO setWx(SignPhoneSetWxDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String codeKey = BaseRedisKeyEnum.PRE_PHONE + currentUserPhoneNotAdmin;

        // 执行
        return SignUtil.setWx(dto.getQrCodeId(), dto.getPhoneCode(), codeKey, null);

    }

    /**
     * 修改微信：发送验证码
     */
    @Override
    public String updateWxSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendUpdateWx(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 修改微信：获取新微信的二维码地址
     */
    @Override
    public GetQrCodeVO updateWxGetQrCodeUrlNew() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, SysQrCodeSceneTypeEnum.WX_BIND);

    }

    /**
     * 修改微信
     */
    @Override
    public SysQrCodeSceneBindVO updateWx(SignPhoneUpdateWxDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String codeKey = BaseRedisKeyEnum.PRE_PHONE + currentUserPhoneNotAdmin;

        // 执行
        return SignUtil.setWx(dto.getQrCodeId(), dto.getPhoneCode(), codeKey, null);

    }

    /**
     * 修改手机-发送新手机验证码
     */
    @Override
    public String updatePhoneSendCodeNew(SignPhoneUpdatePhoneSendCodeNewDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                        BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SysSmsUtil
                                .sendSetPhone(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, dto.getPhone())), currentTenantIdDefault);


    }

    /**
     * 修改手机-发送旧手机验证码
     */
    @Override
    public String updatePhoneSendCodeOld() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendUpdatePhone(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 修改手机
     */
    @Override
    public String updatePhone(SignPhoneUpdatePhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updateAccount(dto.getOldPhoneCode(), dto.getNewPhoneCode(), BaseRedisKeyEnum.PRE_PHONE, BaseRedisKeyEnum.PRE_PHONE, dto.getNewPhone(), null, null);

    }

    /**
     * 忘记密码-发送验证码
     */
    @Override
    public String forgetPasswordSendCode(PhoneNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getPhone(), dto.getTenantId(), null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), true,
                        com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_NOT_REGISTERED, (code) -> SysSmsUtil
                                .sendForgetPassword(SysSmsHelper.getSysSmsSendBO(dto.getTenantId(), code, dto.getPhone())), dto.getTenantId());

    }

    /**
     * 忘记密码
     */
    @Override
    public String forgetPassword(SignPhoneForgetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getPhone(), dto.getTenantId(), null); // 检查：是否可以进行操作

        return SignUtil
                .forgetPassword(dto.getNewPassword(), dto.getOriginNewPassword(), dto.getCode(), BaseRedisKeyEnum.PRE_PHONE,
                        dto.getPhone(), ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), dto.getTenantId());

    }

    /**
     * 账号注销-发送验证码
     */
    @Override
    public String signDeleteSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(BaseRedisKeyEnum.PRE_PHONE, (code, account) -> SysSmsUtil
                .sendSignDelete(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(NotBlankCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.signDelete(dto.getCode(), BaseRedisKeyEnum.PRE_PHONE, null, null);

    }

}
