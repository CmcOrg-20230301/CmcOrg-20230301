package com.cmcorg20230301.be.engine.sign.wx.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
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
import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.*;
import com.cmcorg20230301.be.engine.sign.wx.model.enums.WxSysQrCodeSceneTypeEnum;
import com.cmcorg20230301.be.engine.sign.wx.service.SignWxService;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    SysOtherAppMapper sysOtherAppMapper;

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
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(),
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
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(),
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
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(), () -> {

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
        return SignUtil.getQrCodeUrlWx(UserUtil.getCurrentTenantIdDefault(), true, SysQrCodeSceneTypeEnum.WX_BIND);

    }

    /**
     * 设置密码
     */
    @Override
    public String setPassword(SignWxSetPasswordDTO dto) {


        return null;

    }

    /**
     * 修改密码-获取二维码
     */
    @Override
    public GetQrCodeVO updatePasswordGetQrCodeUrl() {
        return null;
    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignWxUpdatePasswordDTO dto) {
        return null;
    }

    /**
     * 设置登录名-获取二维码
     */
    @Override
    public GetQrCodeVO setSignInNameGetQrCodeUrl() {
        return null;
    }

    /**
     * 设置登录名
     */
    @Override
    public String setSignInName(SignWxSetSignInNameDTO dto) {
        return null;
    }

    /**
     * 修改登录名-获取二维码
     */
    @Override
    public GetQrCodeVO updateSignInNameGetQrCodeUrl() {
        return null;
    }

    /**
     * 修改登录名
     */
    @Override
    public String updateSignInName(SignWxUpdateSignInNameDTO dto) {
        return null;
    }

    /**
     * 设置邮箱：发送验证码
     */
    @Override
    public String setEmailSendCode(SignWxSetEmailSendCodeDTO dto) {
        return null;
    }

    /**
     * 设置邮箱-获取二维码
     */
    @Override
    public GetQrCodeVO setEmailGetQrCodeUrl() {
        return null;
    }

    /**
     * 设置邮箱
     */
    @Override
    public String setEmail(SignWxSetEmailDTO dto) {
        return null;
    }

    /**
     * 修改邮箱：发送验证码
     */
    @Override
    public String updateEmailSendCode(SignWxUpdateEmailSendCodeDTO dto) {
        return null;
    }

    /**
     * 修改邮箱-获取二维码
     */
    @Override
    public GetQrCodeVO updateEmailGetQrCodeUrl() {
        return null;
    }

    /**
     * 修改邮箱
     */
    @Override
    public String updateEmail(SignWxUpdateEmailDTO dto) {
        return null;
    }

    /**
     * 修改微信：获取旧的二维码地址
     */
    @Override
    public GetQrCodeVO updateWxGetQrCodeUrlOld() {
        return null;
    }

    /**
     * 修改微信：获取新的二维码地址
     */
    @Override
    public GetQrCodeVO updateWxGetQrCodeUrlNew(SignWxUpdateWxGetQrCodeUrlNewDTO dto) {
        return null;
    }

    /**
     * 修改微信
     */
    @Override
    public SysQrCodeSceneBindVO updateWx(SignWxUpdateWxDTO dto) {
        return null;
    }

    /**
     * 设置手机：发送验证码
     */
    @Override
    public String setPhoneSendCode(SignWxSetPhoneSendCodeDTO dto) {
        return null;
    }

    /**
     * 设置手机：获取二维码
     */
    @Override
    public GetQrCodeVO setPhoneGetQrCodeUrl(SignWxSetPhoneGetQrCodeUrlDTO dto) {
        return null;
    }

    /**
     * 设置手机
     */
    @Override
    public String setPhone(SignWxSetPhoneDTO dto) {
        return null;
    }

    /**
     * 忘记密码-获取二维码
     */
    @Override
    public GetQrCodeVO forgetPasswordGetQrCodeUrl(SignWxForgetPasswordGetQrCodeUrlDTO dto) {
        return null;
    }

    /**
     * 忘记密码
     */
    @Override
    public String forgetPassword(SignWxForgetPasswordDTO dto) {
        return null;
    }

    /**
     * 账号注销-获取二维码
     */
    @Override
    public GetQrCodeVO signDeleteGetQrCodeUrl() {
        return null;
    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(SignWxSignDeleteDTO dto) {
        return null;
    }

}
