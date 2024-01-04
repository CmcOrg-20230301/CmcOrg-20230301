package com.cmcorg20230301.be.engine.sign.signinname.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserConfigurationDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.service.SysUserConfigurationService;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.sign.signinname.model.dto.*;
import com.cmcorg20230301.be.engine.sign.signinname.service.SignSignInNameService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SignSignInNameServiceImpl implements SignSignInNameService {

    private static final BaseRedisKeyEnum PRE_REDIS_KEY_ENUM = BaseRedisKeyEnum.PRE_SIGN_IN_NAME;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserConfigurationService sysUserConfigurationService;

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

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, null,
                dto.getOldPassword());

    }

    /**
     * 修改账号
     */
    @Override
    public String updateAccount(SignSignInNameUpdateAccountDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.updateAccount(null, null, PRE_REDIS_KEY_ENUM, dto.getNewSignInName(), dto.getCurrentPassword());

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(SignSignInNameSignDeleteDTO dto) {

        SignUtil.checkWillError(PRE_REDIS_KEY_ENUM, null, false, UserUtil.getCurrentTenantIdDefault()); // 检查：是否可以进行操作

        return SignUtil.signDelete(null, PRE_REDIS_KEY_ENUM, dto.getCurrentPassword());

    }

}
