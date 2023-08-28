package com.cmcorg20230301.be.engine.sign.phone.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.phone.model.dto.*;
import com.cmcorg20230301.be.engine.sign.phone.service.SignPhoneService;
import com.cmcorg20230301.be.engine.sms.base.util.SmsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignPhoneServiceImpl implements SignPhoneService {

    private static final RedisKeyEnum PRE_REDIS_KEY_ENUM = RedisKeyEnum.PRE_PHONE;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SecurityProperties securityProperties;

    /**
     * 注册-发送验证码
     */
    @Override
    public String signUpSendCode(PhoneNotBlankDTO dto) {

        checkSignUpEnable(); // 检查：是否允许注册

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SmsUtil.sendSignUp(dto.getPhone(), code));

    }

    /**
     * 检查：是否允许注册
     */
    private void checkSignUpEnable() {

        if (BooleanUtil.isFalse(securityProperties.getPhoneSignUpEnable())) {
            ApiResultVO.errorMsg("操作失败：不允许手机号码注册，请联系管理员");
        }

    }

    /**
     * 注册
     */
    @Override
    public String signUp(SignPhoneSignUpDTO dto) {

        checkSignUpEnable(); // 检查：是否允许注册

        return SignUtil
            .signUp(dto.getPassword(), dto.getOriginPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone(),
                dto.getTenantId());

    }

    /**
     * 手机账号密码登录
     */
    @Override
    public String signInPassword(SignPhoneSignInPasswordDTO dto) {

        return SignUtil
            .signInPassword(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                dto.getPassword(), dto.getPhone(), dto.getTenantId());

    }

    /**
     * 修改密码-发送验证码
     */
    @Override
    public String updatePasswordSendCode() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil
            .getAccountAndSendCode(PRE_REDIS_KEY_ENUM, (code, account) -> SmsUtil.sendUpdatePassword(account, code));

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignPhoneUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil
            .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, dto.getCode(), null);

    }

    /**
     * 修改手机-发送验证码
     */
    @Override
    public String updateAccountSendCode() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String key = PRE_REDIS_KEY_ENUM + currentUserPhoneNotAdmin;

        return SignUtil.sendCode(key, null, true,
            com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_DOES_NOT_EXIST_PLEASE_RE_ENTER,
            (code) -> SmsUtil.sendUpdate(currentUserPhoneNotAdmin, code));

    }

    /**
     * 修改手机
     */
    @Override
    public String updateAccount(SignPhoneUpdateAccountDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil
            .updateAccount(dto.getOldPhoneCode(), dto.getNewPhoneCode(), PRE_REDIS_KEY_ENUM, dto.getNewPhone(), null);

    }

    /**
     * 忘记密码-发送验证码
     */
    @Override
    public String forgetPasswordSendCode(PhoneNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getPhone(), false); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), true,
                com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_NOT_REGISTERED,
                (code) -> SmsUtil.sendForgetPassword(dto.getPhone(), code));

    }

    /**
     * 忘记密码
     */
    @Override
    public String forgetPassword(SignPhoneForgetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getPhone(), false); // 检查：是否可以进行操作

        return SignUtil
            .forgetPassword(dto.getNewPassword(), dto.getOriginNewPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM,
                dto.getPhone(), ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()));

    }

    /**
     * 账号注销-发送验证码
     */
    @Override
    public String signDeleteSendCode() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(PRE_REDIS_KEY_ENUM, (code, account) -> SmsUtil.sendDelete(account, code));

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(NotBlankCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil.signDelete(dto.getCode(), PRE_REDIS_KEY_ENUM, null);

    }

    /**
     * 绑定手机-发送验证码
     */
    @Override
    public String bindAccountSendCode(PhoneNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SmsUtil.sendBind(dto.getPhone(), code));

    }

    /**
     * 绑定手机
     */
    @Override
    public String bindAccount(SignPhoneBindAccountDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone());

    }

    /**
     * 手机验证码登录-发送验证码
     */
    @Override
    public String signInSendCode(PhoneNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), true,
                com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_NOT_REGISTERED,
                (code) -> SmsUtil.sendSignIn(dto.getPhone(), code));

    }

    /**
     * 手机验证码登录
     */
    @Override
    public String signInCode(SignPhoneSignInCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil
            .signInCode(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone(), dto.getTenantId());

    }

}
