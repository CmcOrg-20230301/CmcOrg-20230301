package com.cmcorg20230301.be.engine.jwt.refresh.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.jwt.refresh.model.dto.SysJwtRefreshSignInRefreshTokenDTO;
import com.cmcorg20230301.be.engine.jwt.refresh.service.SysJwtRefreshService;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysJwtRefreshMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysJwtRefreshDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyRsaUtil;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;

import cn.hutool.core.util.StrUtil;

@Service
public class SysJwtRefreshServiceImpl extends ServiceImpl<SysJwtRefreshMapper, SysJwtRefreshDO>
    implements SysJwtRefreshService {

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 通过：refreshToken登录
     */
    @Override
    public SignInVO signInRefreshToken(SysJwtRefreshSignInRefreshTokenDTO dto) {

        // 非对称解密
        String refreshToken = MyRsaUtil.rsaDecrypt(dto.getRefreshToken(), dto.getTenantId());

        if (StrUtil.isBlank(refreshToken)) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, refreshToken);
        }

        SysJwtRefreshDO sysJwtRefreshDO = lambdaQuery().eq(SysJwtRefreshDO::getRefreshToken, refreshToken).one();

        if (sysJwtRefreshDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.LOGIN_EXPIRED, refreshToken);
        }

        SysUserDO sysUserDO =
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, sysJwtRefreshDO.getId())
                .eq(BaseEntityNoIdSuper::getTenantId, sysJwtRefreshDO.getTenantId()).one();

        if (sysUserDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.LOGIN_EXPIRED, refreshToken);
        }

        // 判断：密码错误次数过多，是否被冻结
        SignUtil.checkTooManyPasswordError(sysUserDO.getId());

        // 获取；返回值
        return SignUtil.signInGetJwt(sysUserDO, false);

    }

}
