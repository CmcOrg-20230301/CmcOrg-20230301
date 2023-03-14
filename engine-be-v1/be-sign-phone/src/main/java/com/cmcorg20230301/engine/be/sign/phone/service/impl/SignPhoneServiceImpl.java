package com.cmcorg20230301.engine.be.sign.phone.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.engine.be.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import com.cmcorg20230301.engine.be.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.engine.be.sign.helper.util.SignUtil;
import com.cmcorg20230301.engine.be.sign.phone.configuration.SignPhoneSecurityPermitAllConfiguration;
import com.cmcorg20230301.engine.be.sign.phone.model.dto.*;
import com.cmcorg20230301.engine.be.sign.phone.service.SignPhoneService;
import com.cmcorg20230301.engine.be.tencent.util.SmsTencentUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignPhoneServiceImpl implements SignPhoneService {

    private static RedisKeyEnum PRE_REDIS_KEY_ENUM = RedisKeyEnum.PRE_PHONE;

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
                BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SmsTencentUtil.sendSignUp(dto.getPhone(), code));

    }

    /**
     * 检查：是否允许注册
     */
    private void checkSignUpEnable() {

        if (BooleanUtil.isFalse(securityProperties.getPhoneSignUpEnable())) {
            ApiResultVO.error("操作失败：不允许手机号码注册，请联系管理员");
        }

    }

    /**
     * 注册
     */
    @Override
    @MyTransactional
    public String signUp(SignPhoneSignUpDTO dto) {

        checkSignUpEnable(); // 检查：是否允许注册

        return SignUtil
            .signUp(dto.getPassword(), dto.getOriginPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone());

    }

    /**
     * 手机账号密码登录
     */
    @Override
    public String signInPassword(SignPhoneSignInPasswordDTO dto) {

        return SignUtil
            .signInPassword(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                dto.getPassword(), dto.getPhone());

    }

    /**
     * 修改密码-发送验证码
     */
    @Override
    public String updatePasswordSendCode() {

        return SignUtil.getAccountAndSendCode(PRE_REDIS_KEY_ENUM,
            (code, account) -> SmsTencentUtil.sendUpdatePassword(account, code));

    }

    /**
     * 修改密码
     */
    @Override
    @MyTransactional
    public String updatePassword(SignPhoneUpdatePasswordDTO dto) {

        return SignUtil
            .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, dto.getCode(), null);

    }

    /**
     * 修改手机-发送验证码
     */
    @Override
    public String updateAccountSendCode() {

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String key = PRE_REDIS_KEY_ENUM + currentUserPhoneNotAdmin;

        return SignUtil.sendCode(key, null, true,
            com.cmcorg20230301.engine.be.tencent.exception.BizCodeEnum.PHONE_DOES_NOT_EXIST_PLEASE_RE_ENTER,
            (code) -> SmsTencentUtil.sendUpdate(currentUserPhoneNotAdmin, code));

    }

    /**
     * 修改手机
     */
    @Override
    @MyTransactional
    public String updateAccount(SignPhoneUpdateAccountDTO dto) {

        return SignUtil
            .updateAccount(dto.getOldPhoneCode(), dto.getNewPhoneCode(), PRE_REDIS_KEY_ENUM, dto.getNewPhone(), null);

    }

    /**
     * 忘记密码-发送验证码
     */
    @Override
    public String forgetPasswordSendCode(PhoneNotBlankDTO dto) {

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), true,
                com.cmcorg20230301.engine.be.tencent.exception.BizCodeEnum.PHONE_NOT_REGISTERED,
                (code) -> SmsTencentUtil.sendForgetPassword(dto.getPhone(), code));

    }

    /**
     * 忘记密码
     */
    @Override
    @MyTransactional
    public String forgetPassword(SignPhoneForgetPasswordDTO dto) {

        return SignUtil
            .forgetPassword(dto.getNewPassword(), dto.getOriginNewPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM,
                dto.getPhone(), ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()));

    }

    /**
     * 账号注销-发送验证码
     */
    @Override
    public String signDeleteSendCode() {

        // 如果有更高级的账号注销-发送验证码，则禁用低级的账号注销-发送验证码
        SignUtil.checkSignLevel(SignPhoneSecurityPermitAllConfiguration.SIGN_LEVEL);

        return SignUtil
            .getAccountAndSendCode(PRE_REDIS_KEY_ENUM, (code, account) -> SmsTencentUtil.sendDelete(account, code));

    }

    /**
     * 账号注销
     */
    @Override
    @MyTransactional
    public String signDelete(NotBlankCodeDTO dto) {

        // 如果有更高级的账号注销，则禁用低级的账号注销
        SignUtil.checkSignLevel(SignPhoneSecurityPermitAllConfiguration.SIGN_LEVEL);

        return SignUtil.signDelete(dto.getCode(), PRE_REDIS_KEY_ENUM, null);

    }

    /**
     * 绑定手机-发送验证码
     */
    @Override
    public String bindAccountSendCode(PhoneNotBlankDTO dto) {

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SmsTencentUtil.sendBind(dto.getPhone(), code));

    }

    /**
     * 绑定手机
     */
    @Override
    @MyTransactional
    public String bindAccount(SignPhoneBindAccountDTO dto) {

        return SignUtil.bindAccount(dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone());

    }

    /**
     * 手机验证码登录-发送验证码
     */
    @Override
    public String signInSendCode(PhoneNotBlankDTO dto) {

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), true,
                com.cmcorg20230301.engine.be.tencent.exception.BizCodeEnum.PHONE_NOT_REGISTERED,
                (code) -> SmsTencentUtil.sendSignIn(dto.getPhone(), code));

    }

    /**
     * 手机验证码登录
     */
    @Override
    public String signInCode(SignPhoneSignInCodeDTO dto) {

        return SignUtil
            .signInCode(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone());

    }

}
