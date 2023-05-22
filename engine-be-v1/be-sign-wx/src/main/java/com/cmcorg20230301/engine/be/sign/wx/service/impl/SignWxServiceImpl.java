package com.cmcorg20230301.engine.be.sign.wx.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.engine.be.sign.helper.util.SignUtil;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInMiniProgramCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.model.dto.SignInMiniProgramPhoneCodeDTO;
import com.cmcorg20230301.engine.be.sign.wx.service.SignWxService;
import com.cmcorg20230301.engine.be.util.util.NicknameUtil;
import com.cmcorg20230301.engine.be.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.engine.be.wx.model.vo.WxPhoneByCodeVO;
import com.cmcorg20230301.engine.be.wx.util.WxUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignWxServiceImpl implements SignWxService {

    private static final RedisKeyEnum PRE_REDIS_KEY_ENUM = RedisKeyEnum.PRE_WX_OPEN_ID;

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 小程序：手机号 code登录
     */
    @Override
    public String signInMiniProgramPhoneCode(SignInMiniProgramPhoneCodeDTO dto) {

        // 获取：用户手机号
        WxPhoneByCodeVO.WxPhoneInfoVO wxPhoneInfoVO = WxUtil.getWxMiniProgramPhoneInfoVOByCode(dto.getPhoneCode());

        // 直接通过：手机号登录
        return SignUtil.signInAccount(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getPhone, wxPhoneInfoVO.getPhoneNumber()),
            RedisKeyEnum.PRE_PHONE, wxPhoneInfoVO.getPhoneNumber(), getWxSysUserInfoDO());

    }

    /**
     * 小程序：微信 code登录
     */
    @Override
    public String signInMiniProgramCode(SignInMiniProgramCodeDTO dto) {

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxMiniProgramOpenIdVOByCode(dto.getCode());

        // 直接通过：微信 openId登录
        return SignUtil.signInAccount(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid()),
            PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(), getWxSysUserInfoDO());

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

        WxOpenIdVO wxOpenIdVO = WxUtil.getWxBrowserOpenIdVOByCode(dto.getCode());

        // 直接通过：微信 openId登录
        return SignUtil.signInAccount(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, wxOpenIdVO.getOpenid()),
            PRE_REDIS_KEY_ENUM, wxOpenIdVO.getOpenid(), getWxSysUserInfoDO());

    }

}
