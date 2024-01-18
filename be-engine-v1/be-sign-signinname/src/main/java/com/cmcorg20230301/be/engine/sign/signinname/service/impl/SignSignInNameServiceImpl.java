package com.cmcorg20230301.be.engine.sign.signinname.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.util.MyEmailUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.SysQrCodeSceneBindExistUserDTO;
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
                .signUp(dto.getPassword(), dto.getOriginPassword(), null, PRE_REDIS_KEY_ENUM, dto.getSignInName(),
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

        return SignUtil.updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, null,
                dto.getOldPassword());

    }

    /**
     * 修改账号
     */
    @Override
    public String updateSignInName(SignSignInNameUpdateSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.updateAccount(null, null, PRE_REDIS_KEY_ENUM, dto.getNewSignInName(), dto.getCurrentPassword(), null);

    }

    /**
     * 设置邮箱：发送验证码
     */
    @Override
    public String setEmailSendCode(SignSignInNameSetEmailSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, false, currentTenantIdDefault));

    }

    /**
     * 设置邮箱
     */
    @Override
    public String setEmail(SignSignInNameSetEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getEmail(), null);

    }

    /**
     * 设置微信：获取二维码地址
     */
    @Override
    public GetQrCodeVO setWxGetQrCodeUrl(SignSignInNameSetWxGetQrCodeUrlDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, SysQrCodeSceneTypeEnum.WX_BIND);

    }

    /**
     * 设置微信
     */
    @Override
    public SysQrCodeSceneBindVO setWx(NotNullId notNullId) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.setWx(notNullId.getId());

    }

    /**
     * 设置微信-存在用户
     */
    @Override
    public String setWxExistUser(SysQrCodeSceneBindExistUserDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.setWxExistUser(dto);

    }

    /**
     * 设置手机：发送验证码
     */
    @Override
    public String setPhoneSendCode(SignSignInNameSetPhoneSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = PRE_REDIS_KEY_ENUM + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                        BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SysSmsUtil
                                .sendBind(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, dto.getPhone())));

    }

    /**
     * 设置手机
     */
    @Override
    public String setPhone(SignSignInNameSetPhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.bindAccount(dto.getCode(), PRE_REDIS_KEY_ENUM, dto.getPhone(), null);

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(SignSignInNameSignDeleteDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        return SignUtil.signDelete(null, PRE_REDIS_KEY_ENUM, dto.getCurrentPassword(), null);

    }

}
