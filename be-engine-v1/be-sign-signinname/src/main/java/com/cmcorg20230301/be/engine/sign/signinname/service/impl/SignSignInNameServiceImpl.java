package com.cmcorg20230301.be.engine.sign.signinname.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;
import com.cmcorg20230301.be.engine.sign.signinname.service.SignSignInNameService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignSignInNameServiceImpl implements SignSignInNameService {

    private static final RedisKeyEnum PRE_REDIS_KEY_ENUM = RedisKeyEnum.PRE_SIGN_IN_NAME;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SecurityProperties securityProperties;

    /**
     * 注册
     */
    @Override
    public String signUp(SignSignInNameSignUpDTO dto) {

        if (BooleanUtil.isFalse(securityProperties.getSignInNameSignUpEnable())) {
            ApiResultVO.errorMsg("操作失败：不允许用户名注册，请联系管理员");
        }

        return SignUtil
            .signUp(dto.getPassword(), dto.getOriginPassword(), null, PRE_REDIS_KEY_ENUM, dto.getSignInName());

    }

    /**
     * 账号密码登录
     */
    @Override
    public String signInPassword(SignSignInNameSignInPasswordDTO dto) {

        return SignUtil.signInPassword(
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getSignInName, dto.getSignInName()),
            dto.getPassword(), dto.getSignInName());

    }

    /**
     * 修改密码
     */
    @Override
    public String updatePassword(SignSignInNameUpdatePasswordDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil.updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, null,
            dto.getOldPassword());

    }

    /**
     * 修改账号
     */
    @Override
    public String updateAccount(SignSignInNameUpdateAccountDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil.updateAccount(null, null, PRE_REDIS_KEY_ENUM, dto.getNewSignInName(), dto.getCurrentPassword());

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(SignSignInNameSignDeleteDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false); // 检查：是否可以进行操作

        return SignUtil.signDelete(null, PRE_REDIS_KEY_ENUM, dto.getCurrentPassword());

    }

}
