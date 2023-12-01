package com.cmcorg20230301.be.engine.sign.wx.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxPhoneByCodeVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxUserInfoVO;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.util.MyJwtUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.service.SignWxService;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignWxServiceImpl implements SignWxService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_WX_OPEN_ID;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserInfoMapper sysUserInfoMapper;

    /**
     * 小程序：手机号 code登录
     */
    @Override
    public String signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto) {

        // 获取：用户手机号
        WxPhoneByCodeVO.WxPhoneInfoVO wxPhoneInfoVO =
            WxUtil.getWxMiniProgramPhoneInfoVoByCode(dto.getTenantId(), dto.getPhoneCode(), dto.getAppId());

        // 直接通过：手机号登录
        return SignUtil.signInAccount(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, wxPhoneInfoVO.getPhoneNumber()),
            BaseRedisKeyEnum.PRE_PHONE, wxPhoneInfoVO.getPhoneNumber(), SignWxServiceImpl::getWxSysUserInfoDO,
            dto.getTenantId(), accountMap -> {

                accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

            });

    }

    /**
     * 小程序：微信 code登录
     */
    @Override
    public String signInMiniProgramCode(SignInMiniProgramCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxMiniProgramOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        // 直接通过：微信 openId登录
        return SignUtil.signInAccount(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid())
                .eq(SysUserDO::getWxAppId, dto.getAppId()), PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(),
            SignWxServiceImpl::getWxSysUserInfoDO, dto.getTenantId(), accountMap -> {

                accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

            });

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
    public String signInBrowserCode(SignInBrowserCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxBrowserOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        // 直接通过：微信 openId登录
        return SignUtil.signInAccount(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid())
                .eq(SysUserDO::getWxAppId, dto.getAppId()), PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(),
            SignWxServiceImpl::getWxSysUserInfoDO, dto.getTenantId(), accountMap -> {

                accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

            });

    }

    /**
     * 浏览器：微信 code登录，可以获取用户的基础信息
     */
    @Override
    public String signInBrowserCodeUserInfo(SignInBrowserCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxBrowserOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        // 是否是：注册
        CallBack<Boolean> signUpFlagCallBack = new CallBack<>(false);

        Long tenantId = SysTenantUtil.getTenantId(dto.getTenantId());

        // 直接通过：微信 openId登录
        String jwtStr = SignUtil.signInAccount(
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

            });

        if (BooleanUtil.isFalse(signUpFlagCallBack.getValue())) {

            JWT jwt = JWT.of(MyJwtUtil.getJwtStrByHeadAuthorization(jwtStr));

            // 获取：userId的值
            Long userId = MyJwtUtil.getPayloadMapUserIdValue(jwt.getPayload().getClaimsJson());

            boolean exists = ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, userId)
                .likeLeft(SysUserInfoDO::getNickname, WX_SYS_USER_INFO_NICKNAME_PRE).exists();

            if (exists) {

                WxUserInfoVO wxUserInfoVO = WxUtil
                    .getWxUserInfoByBrowserAccessToken(wxOpenIdVO.getAccessToken(), wxOpenIdVO.getOpenid(), tenantId,
                        dto.getAppId());

                // 更新：用户的昵称
                ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, userId)
                    .set(SysUserInfoDO::getNickname, wxUserInfoVO.getNickname()).update();

            }

        }

        return jwtStr;

    }

}
