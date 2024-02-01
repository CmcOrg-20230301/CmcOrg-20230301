package com.cmcorg20230301.be.engine.sign.wx.work.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxWorkOpenIdVO;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.util.SysUserInfoUtil;
import com.cmcorg20230301.be.engine.sign.helper.model.dto.SignInBrowserCodeDTO;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.wx.work.service.SignWxWorkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignWxWorkServiceImpl implements SignWxWorkService {

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 浏览器：企业微信 code登录
     */
    @Override
    public SignInVO signInBrowserCode(SignInBrowserCodeDTO dto) {

        WxWorkOpenIdVO wxWorkOpenIdVO = WxUtil.getWxWorkBrowserOpenIdVoByCode(dto.getTenantId(), dto.getCode(), dto.getAppId());

        String openId = wxWorkOpenIdVO.getUserid();

        if (StrUtil.isBlank(openId)) {

            openId = wxWorkOpenIdVO.getExternalUserid();

        }

        // 直接通过：企业微信 openId登录
        return SignUtil.signInAccount(
                ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, openId)
                        .eq(SysUserDO::getWxAppId, dto.getAppId()), BaseRedisKeyEnum.PRE_WX_OPEN_ID, openId,
                SysUserInfoUtil::getWxWorkSysUserInfoDO, dto.getTenantId(), accountMap -> {

                    accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getAppId());

                }, null);

    }

}
