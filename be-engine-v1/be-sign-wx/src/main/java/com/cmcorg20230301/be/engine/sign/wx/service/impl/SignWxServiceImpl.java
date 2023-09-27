package com.cmcorg20230301.be.engine.sign.wx.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;
import com.cmcorg20230301.be.engine.sign.wx.service.SignWxService;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import com.cmcorg20230301.be.engine.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.be.engine.wx.model.vo.WxPhoneByCodeVO;
import com.cmcorg20230301.be.engine.wx.util.WxUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignWxServiceImpl implements SignWxService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_WX_OPEN_ID;

    @Resource
    SysUserMapper sysUserMapper;

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
            BaseRedisKeyEnum.PRE_PHONE, wxPhoneInfoVO.getPhoneNumber(), getWxSysUserInfoDO(), dto.getTenantId(),
            accountMap -> {

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
            getWxSysUserInfoDO(), dto.getTenantId(), accountMap -> {

                accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

            });

    }

    /**
     * 获取：带有昵称的 用户对象
     */
    @NotNull
    private SysUserInfoDO getWxSysUserInfoDO() {

        SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
        sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname("微信用户"));

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
            getWxSysUserInfoDO(), dto.getTenantId(), accountMap -> {

                accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

            });

    }

}
