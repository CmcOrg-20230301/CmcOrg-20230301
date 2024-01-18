package com.cmcorg20230301.be.engine.sign.phone.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserConfigurationDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
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

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                        BizCodeEnum.PHONE_HAS_BEEN_REGISTERED,
                        (code) -> SysSmsUtil.sendSignUp(SysSmsHelper.getSysSmsSendBO(dto.getTenantId(), code, dto.getPhone())));

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
                .signUp(dto.getPassword(), dto.getOriginPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone(),
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
     * 修改密码-发送验证码
     */
    @Override
    public String updatePasswordSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(PRE_REDIS_KEY_ENUM, (code, account) -> SysSmsUtil
                .sendUpdatePassword(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignPhoneUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, dto.getCode(), null);

    }

    /**
     * 修改手机-发送验证码
     */
    @Override
    public String updatePhoneSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

        String key = PRE_REDIS_KEY_ENUM + currentUserPhoneNotAdmin;

        return SignUtil.sendCode(key, null, true,
                com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_DOES_NOT_EXIST_PLEASE_RE_ENTER,
                (code) -> SysSmsUtil
                        .sendUpdate(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, currentUserPhoneNotAdmin)));

    }

    /**
     * 修改手机
     */
    @Override
    public String updatePhone(SignPhoneUpdatePhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil
                .updateAccount(dto.getOldPhoneCode(), dto.getNewPhoneCode(), PRE_REDIS_KEY_ENUM, dto.getNewPhone(), null, null);

    }

    /**
     * 忘记密码-发送验证码
     */
    @Override
    public String forgetPasswordSendCode(PhoneNotBlankDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getPhone(), dto.getTenantId(), null); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), true,
                        com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_NOT_REGISTERED, (code) -> SysSmsUtil
                                .sendForgetPassword(SysSmsHelper.getSysSmsSendBO(dto.getTenantId(), code, dto.getPhone())));

    }

    /**
     * 忘记密码
     */
    @Override
    public String forgetPassword(SignPhoneForgetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, dto.getPhone(), dto.getTenantId(), null); // 检查：是否可以进行操作

        return SignUtil
                .forgetPassword(dto.getNewPassword(), dto.getOriginNewPassword(), dto.getCode(), PRE_REDIS_KEY_ENUM,
                        dto.getPhone(), ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()));

    }

    /**
     * 手机验证码登录-发送验证码
     */
    @Override
    public String signInSendCode(PhoneNotBlankDTO dto) {

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), null,
                        com.cmcorg20230301.be.engine.sms.base.exception.BizCodeEnum.PHONE_NOT_REGISTERED, (code) -> SysSmsUtil
                                .sendSignIn(SysSmsHelper.getSysSmsSendBO(dto.getTenantId(), code, dto.getPhone())));

    }

    /**
     * 手机验证码登录
     */
    @Override
    public SignInVO signInCode(SignPhoneSignInCodeDTO dto) {

        return SignUtil
                .signInCode(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()),
                        dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone(), dto.getTenantId(), null);

    }

    /**
     * 账号注销-发送验证码
     */
    @Override
    public String signDeleteSendCode() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        return SignUtil.getAccountAndSendCode(PRE_REDIS_KEY_ENUM, (code, account) -> SysSmsUtil
                .sendDelete(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, account)));

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(NotBlankCodeDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.signDelete(dto.getCode(), PRE_REDIS_KEY_ENUM, null, null);

    }

}
