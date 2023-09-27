package com.cmcorg20230301.be.engine.sign.email.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.util.MyEmailUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;
import com.cmcorg20230301.be.engine.sign.email.service.SignEmailService;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignEmailServiceImpl implements SignEmailService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_EMAIL;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SecurityProperties securityProperties;

    /**
     * 注册-发送验证码
     */
    @Override
    public String signUpSendCode(EmailNotBlankDTO dto) {

        checkSignUpEnable(); // 检查：是否允许注册

        String key = PRE_REDIS_KEY_ENUM + dto.getEmail();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED,
                (code) -> MyEmailUtil.send(dto.getEmail(), EmailMessageEnum.SIGN_UP, code, false));

    }

    /**
     * 检查：是否允许注册
     */
    private void checkSignUpEnable() {

        if (BooleanUtil.isFalse(securityProperties.getEmailSignUpEnable())) {
            ApiResultVO.errorMsg("操作失败：不允许邮箱注册，请联系管理员");
        }

    }

    /**
     * 注册
     */
    @Override
    public String signUp(SignEmailSignUpDTO dto) {

        checkSignUpEnable(); // 检查：是否允许注册

        return SignUtil
            .signUp(dto.getPassword(), dto.getOriginPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getEmail(),
                dto.getTenantId());

    }

    /**
     * 邮箱账号密码登录
     */
    @Override
    public String signInPassword(SignEmailSignInPasswordDTO dto) {

        return SignUtil
            .signInPassword(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()),
                dto.getPassword(), dto.getEmail(), dto.getTenantId());

    }

    /**
     * 修改密码-发送验证码
     */
    @Override
    public String updatePasswordSendCode() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(PRE_REDIS_KEY_ENUM,
            (code, account) -> MyEmailUtil.send(account, EmailMessageEnum.UPDATE_PASSWORD, code, false));

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignEmailUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil
            .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, dto.getCode(), null);

    }

    /**
     * 修改邮箱-发送验证码
     */
    @Override
    public String updateAccountSendCode() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, true, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

        String key = PRE_REDIS_KEY_ENUM + currentUserEmailNotAdmin;

        return SignUtil.sendCode(key, null, true,
            com.cmcorg20230301.be.engine.email.exception.BizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER,
            (code) -> MyEmailUtil.send(currentUserEmailNotAdmin, EmailMessageEnum.UPDATE_EMAIL, code, false));

    }

    /**
     * 修改邮箱
     */
    @Override
    public String updateAccount(SignEmailUpdateAccountDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, true, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil
            .updateAccount(dto.getOldEmailCode(), dto.getNewEmailCode(), PRE_REDIS_KEY_ENUM, dto.getNewEmail(), null);

    }

    /**
     * 忘记密码-发送验证码
     */
    @Override
    public String forgetPasswordSendCode(EmailNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getEmail(), false, dto.getTenantId()); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getEmail();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), true,
                com.cmcorg20230301.be.engine.email.exception.BizCodeEnum.EMAIL_NOT_REGISTERED,
                (code) -> MyEmailUtil.send(dto.getEmail(), EmailMessageEnum.FORGET_PASSWORD, code, false));

    }

    /**
     * 忘记密码
     */
    @Override
    public String forgetPassword(SignEmailForgetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getEmail(), false, dto.getTenantId()); // 检查：是否可以进行操作

        return SignUtil
            .forgetPassword(dto.getNewPassword(), dto.getOriginNewPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM,
                dto.getEmail(), ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()));

    }

    /**
     * 账号注销-发送验证码
     */
    @Override
    public String signDeleteSendCode() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(PRE_REDIS_KEY_ENUM,
            (code, account) -> MyEmailUtil.send(account, EmailMessageEnum.SIGN_DELETE, code, false));

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(NotBlankCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.signDelete(dto.getCode(), PRE_REDIS_KEY_ENUM, null);

    }

    /**
     * 绑定邮箱-发送验证码
     */
    @Override
    public String bindAccountSendCode(EmailNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, true, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getEmail();

        return SignUtil
            .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED,
                (code) -> MyEmailUtil.send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, false));

    }

    /**
     * 绑定邮箱
     */
    @Override
    public String bindAccount(SignEmailBindAccountDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, true, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getEmail());

    }

}
