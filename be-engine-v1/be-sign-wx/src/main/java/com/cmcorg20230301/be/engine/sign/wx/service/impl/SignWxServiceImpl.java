package com.cmcorg20230301.be.engine.sign.wx.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.util.MyEmailUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxPhoneByCodeVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxUserInfoVO;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.security.util.MyJwtUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.*;
import com.cmcorg20230301.be.engine.sign.wx.model.enums.WxSysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.sign.wx.service.SignWxService;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsHelper;
import com.cmcorg20230301.be.engine.sms.base.util.SysSmsUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignWxServiceImpl implements SignWxService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_WX_OPEN_ID;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserInfoMapper sysUserInfoMapper;

    @Resource
    RedissonClient redissonClient;

    /**
     * 小程序：手机号 code登录
     */
    @Override
    public SignInVO signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto) {

        // 获取：用户手机号
        WxPhoneByCodeVO.WxPhoneInfoVO wxPhoneInfoVO =
                WxUtil.getWxMiniProgramPhoneInfoVoByCode(dto.getTenantId(), dto.getPhoneCode(), dto.getAppId());

        // 直接通过：手机号登录
        return SignUtil.signInAccount(
                ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, wxPhoneInfoVO.getPhoneNumber()),
                BaseRedisKeyEnum.PRE_PHONE, wxPhoneInfoVO.getPhoneNumber(), SignWxServiceImpl::getWxSysUserInfoDO,
                dto.getTenantId(), accountMap -> {

                    accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

                }, null);

    }

    /**
     * 小程序：微信 code登录
     */
    @Override
    public SignInVO signInMiniProgramCode(SignInMiniProgramCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxMiniProgramOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        // 直接通过：微信 openId登录
        return SignUtil.signInAccount(
                ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid())
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), BaseRedisKeyEnum.PRE_WX_OPEN_ID, wxOpenIdVO.getOpenid(),
                SignWxServiceImpl::getWxSysUserInfoDO, dto.getTenantId(), accountMap -> {

                    accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

                }, null);

    }

    // 微信用户：昵称前缀
    public static final String WX_SYS_USER_INFO_NICKNAME_PRE = "微信用户";

    /**
     * 获取：带有昵称的 用户对象
     */
    @NotNull
    public static SysUserInfoDO getWxSysUserInfoDO() {

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
        sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname(WX_SYS_USER_INFO_NICKNAME_PRE));

        return sysUserInfoDO;

    }

    /**
     * 浏览器：微信 code登录
     */
    @Override
    public SignInVO signInBrowserCode(SignInBrowserCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxBrowserOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        // 直接通过：微信 openId登录
        return SignUtil.signInAccount(
                ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid())
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), BaseRedisKeyEnum.PRE_WX_OPEN_ID, wxOpenIdVO.getOpenid(),
                SignWxServiceImpl::getWxSysUserInfoDO, dto.getTenantId(), accountMap -> {

                    accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

                }, null);

    }

    /**
     * 浏览器：微信 code登录，可以获取用户的基础信息
     */
    @Override
    public SignInVO signInBrowserCodeUserInfo(SignInBrowserCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxBrowserOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        // 是否是：注册
        CallBack<Boolean> signUpFlagCallBack = new CallBack<>(false);

        Long tenantId = SysTenantUtil.getTenantId(dto.getTenantId());

        // 直接通过：微信 openId登录
        SignInVO signInVO = SignUtil.signInAccount(
                ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid())
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), BaseRedisKeyEnum.PRE_WX_OPEN_ID, wxOpenIdVO.getOpenid(), () -> {

                    WxUserInfoVO wxUserInfoVO = WxUtil
                            .getWxUserInfoByBrowserAccessToken(wxOpenIdVO.getAccessToken(), wxOpenIdVO.getOpenid(), tenantId,
                                    dto.getAppId());

                    SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

                    sysUserInfoDO.setNickname(wxUserInfoVO.getNickname());

                    signUpFlagCallBack.setValue(true);

                    return sysUserInfoDO;

                }, dto.getTenantId(), accountMap -> {

                    accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

                }, null);

        if (BooleanUtil.isFalse(signUpFlagCallBack.getValue())) {

            JWT jwt = JWT.of(MyJwtUtil.getJwtStrByHeadAuthorization(signInVO.getJwt()));

            // 获取：userId的值
            Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

            boolean exists = ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, userId)
                    .likeRight(SysUserInfoDO::getNickname, WX_SYS_USER_INFO_NICKNAME_PRE).exists();

            if (exists) {

                WxUserInfoVO wxUserInfoVO = WxUtil
                        .getWxUserInfoByBrowserAccessToken(wxOpenIdVO.getAccessToken(), wxOpenIdVO.getOpenid(), tenantId,
                                dto.getAppId());

                // 更新：用户的昵称
                ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, userId)
                        .set(SysUserInfoDO::getNickname, wxUserInfoVO.getNickname()).update();

            }

        }

        return signInVO;

    }

    /**
     * 扫码登录：获取二维码
     */
    @Override
    @Nullable
    public GetQrCodeVO signInGetQrCodeUrl(UserSignBaseDTO dto, boolean getQrCodeUrlFlag) {

        // 执行
        return SignUtil.getQrCodeUrlWx(dto.getTenantId(), getQrCodeUrlFlag, WxSysQrCodeSceneTypeEnum.WX_SIGN_IN);

    }

    /**
     * 扫码登录-二维码 id
     */
    @Override
    public SignInVO signInByQrCodeId(NotNullId notNullId) {

        return redissonClient.<SignInVO>getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SIGN.name() + notNullId.getId()).getAndDelete();

    }

    /**
     * 设置密码-获取二维码
     */
    @Override
    public GetQrCodeVO setPasswordGetQrCodeUrl() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, WxSysQrCodeSceneTypeEnum.WX_SET_PASSWORD);

    }

    /**
     * 设置密码
     */
    @Override
    public SysQrCodeSceneBindVO setPassword(SignWxSetPasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_PASSWORD.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 修改密码
            SignUtil
                    .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), BaseRedisKeyEnum.PRE_WX_OPEN_ID, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 修改密码-获取二维码
     */
    @Override
    public GetQrCodeVO updatePasswordGetQrCodeUrl() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, WxSysQrCodeSceneTypeEnum.WX_UPDATE_PASSWORD);

    }

    /**
     * 修改密码
     */
    @Override
    public SysQrCodeSceneBindVO updatePassword(SignWxUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_PASSWORD.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 修改密码
            SignUtil
                    .updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), BaseRedisKeyEnum.PRE_WX_OPEN_ID, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 设置登录名-获取二维码
     */
    @Override
    public GetQrCodeVO setSignInNameGetQrCodeUrl(SignWxSetSignInNameGetQrCodeUrlDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：登录名是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()), false, BizCodeEnum.SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER, currentTenantIdDefault);

        // 执行
        return SignUtil.getQrCodeUrlWx(currentTenantIdDefault, true, WxSysQrCodeSceneTypeEnum.WX_SET_SIGN_IN_NAME);

    }

    /**
     * 设置登录名
     */
    @Override
    public SysQrCodeSceneBindVO setSignInName(SignWxSetSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_SIGN_IN_NAME.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 设置登录名
            SignUtil.bindAccount(null, BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(), null, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 修改登录名-获取二维码
     */
    @Override
    public GetQrCodeVO updateSignInNameGetQrCodeUrl(SignWxUpdateSignInNameGetQrCodeUrlDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：登录名是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()), false, BizCodeEnum.SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER, currentTenantIdDefault);

        // 执行
        return SignUtil.getQrCodeUrlWx(currentTenantIdDefault, true, WxSysQrCodeSceneTypeEnum.WX_UPDATE_SIGN_IN_NAME);

    }

    /**
     * 修改登录名
     */
    @Override
    public SysQrCodeSceneBindVO updateSignInName(SignWxUpdateSignInNameDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_SIGN_IN_NAME.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 设置登录名
            SignUtil.bindAccount(null, BaseRedisKeyEnum.PRE_SIGN_IN_NAME, dto.getSignInName(), null, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 设置邮箱：发送验证码
     */
    @Override
    public String setEmailSendCode(SignWxSetEmailSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 设置邮箱-获取二维码
     */
    @Override
    public GetQrCodeVO setEmailGetQrCodeUrl(SignWxSetEmailGetQrCodeUrlDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：邮箱是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false, BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, currentTenantIdDefault);

        // 执行
        return SignUtil.getQrCodeUrlWx(currentTenantIdDefault, true, WxSysQrCodeSceneTypeEnum.WX_SET_EMAIL);

    }

    /**
     * 设置邮箱
     */
    @Override
    public SysQrCodeSceneBindVO setEmail(SignWxSetEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_EMAIL.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 设置邮箱
            SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_EMAIL, dto.getEmail(), null, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 修改邮箱：发送验证码
     */
    @Override
    public String updateEmailSendCode(SignWxUpdateEmailSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_EMAIL + dto.getEmail();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false,
                        BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, (code) -> MyEmailUtil
                                .send(dto.getEmail(), EmailMessageEnum.BIND_EMAIL, code, currentTenantIdDefault), currentTenantIdDefault);

    }

    /**
     * 修改邮箱-获取二维码
     */
    @Override
    public GetQrCodeVO updateEmailGetQrCodeUrl(SignWxUpdateEmailGetQrCodeUrlDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：邮箱是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, dto.getEmail()), false, BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED, currentTenantIdDefault);

        // 执行
        return SignUtil.getQrCodeUrlWx(currentTenantIdDefault, true, WxSysQrCodeSceneTypeEnum.WX_UPDATE_EMAIL);

    }

    /**
     * 修改邮箱
     */
    @Override
    public SysQrCodeSceneBindVO updateEmail(SignWxUpdateEmailDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_EMAIL.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 设置邮箱
            SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_EMAIL, dto.getEmail(), null, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 修改微信：获取旧的二维码地址
     */
    @Override
    public GetQrCodeVO updateWxGetQrCodeUrlOld() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(currentTenantIdDefault, true, WxSysQrCodeSceneTypeEnum.WX_UPDATE_WX);

    }

    /**
     * 修改微信：获取新的二维码地址
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
    public SysQrCodeSceneBindVO updateWx(SignWxUpdateWxDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_UPDATE_WX.name() + dto.getOldQrCodeId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 修改微信
            return SignUtil.setWx(dto.getNewQrCodeId(), null, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 设置手机：发送验证码
     */
    @Override
    public String setPhoneSendCode(SignWxSetPhoneSendCodeDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        String key = BaseRedisKeyEnum.PRE_PHONE + dto.getPhone();

        return SignUtil
                .sendCode(key, ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false,
                        BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, (code) -> SysSmsUtil
                                .sendSetPhone(SysSmsHelper.getSysSmsSendBO(currentTenantIdDefault, code, dto.getPhone())), currentTenantIdDefault);

    }

    /**
     * 设置手机：获取二维码
     */
    @Override
    public GetQrCodeVO setPhoneGetQrCodeUrl(SignWxSetPhoneGetQrCodeUrlDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, currentTenantIdDefault, null); // 检查：是否可以进行操作

        // 检查：手机是否被占用
        SignUtil.checkAccountExistWillError(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, dto.getPhone()), false, BizCodeEnum.PHONE_HAS_BEEN_REGISTERED, currentTenantIdDefault);

        // 执行
        return SignUtil.getQrCodeUrlWx(currentTenantIdDefault, true, WxSysQrCodeSceneTypeEnum.WX_SET_PHONE);

    }

    /**
     * 设置手机
     */
    @Override
    public SysQrCodeSceneBindVO setPhone(SignWxSetPhoneDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_SET_PHONE.name() + dto.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 设置邮箱
            SignUtil.bindAccount(dto.getCode(), BaseRedisKeyEnum.PRE_PHONE, dto.getPhone(), null, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 账号注销-获取二维码
     */
    @Override
    public GetQrCodeVO signDeleteGetQrCodeUrl() {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        // 执行
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, WxSysQrCodeSceneTypeEnum.WX_SIGN_DELETE);

    }

    /**
     * 账号注销
     */
    @Override
    public SysQrCodeSceneBindVO signDelete(NotNullId notNullId) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, UserUtil.getCurrentTenantIdDefault(), null); // 检查：是否可以进行操作

        RBucket<String> bucket = redissonClient.getBucket(BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_WX_SIGN_DELETE.name() + notNullId.getId());

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        boolean deleteFlag = bucket.delete();

        if (deleteFlag) {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            // 账号注销
            SignUtil.signDelete(null, BaseRedisKeyEnum.PRE_WX_OPEN_ID, null, null);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        }

        return sysQrCodeSceneBindVO;

    }

}
