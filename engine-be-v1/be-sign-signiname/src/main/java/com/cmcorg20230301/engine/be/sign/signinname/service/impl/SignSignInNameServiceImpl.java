package com.cmcorg20230301.engine.be.sign.signinname.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import com.cmcorg20230301.engine.be.sign.helper.util.SignUtil;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.*;
import com.cmcorg20230301.engine.be.sign.signinname.service.SignSignInNameService;
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
            ApiResultVO.error("操作失败：不允许用户名注册，请联系管理员");
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

        // 检查：登录名，是否可以执行操作
        checkSignNameCanBeExecutedAndError();

        return SignUtil.updatePassword(dto.getNewPassword(), dto.getOriginNewPassword(), PRE_REDIS_KEY_ENUM, null,
            dto.getOldPassword());

    }

    /**
     * 修改账号
     */
    @Override
    public String updateAccount(SignSignInNameUpdateAccountDTO dto) {

        return SignUtil.updateAccount(null, null, PRE_REDIS_KEY_ENUM, dto.getNewSignInName(), dto.getCurrentPassword());

    }

    /**
     * 账号注销
     */
    @Override
    public String signDelete(SignSignInNameSignDeleteDTO dto) {

        // 检查：登录名，是否可以执行操作
        checkSignNameCanBeExecutedAndError();

        return SignUtil.signDelete(null, PRE_REDIS_KEY_ENUM, dto.getCurrentPassword());

    }

    /**
     * 检查：登录名，是否可以执行操作，如果不可以，则抛出异常
     */
    private void checkSignNameCanBeExecutedAndError() {

        if (checkSignNameNotCanBeExecuted()) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

    }

    /**
     * 检查：登录名，是否不可以执行操作
     */
    private boolean checkSignNameNotCanBeExecuted() {

        // 判断是否有：邮箱或者手机号，或者密码等于空，即：密码不能为空，并且不能有手机或者邮箱
        return ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, UserUtil.getCurrentUserIdNotAdmin())
            .and(
                i -> i.eq(SysUserDO::getPassword, "").or().ne(SysUserDO::getEmail, "").or().ne(SysUserDO::getPhone, ""))
            .exists();

    }

}
