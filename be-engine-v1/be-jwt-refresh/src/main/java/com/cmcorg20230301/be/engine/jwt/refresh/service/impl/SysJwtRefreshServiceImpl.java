package com.cmcorg20230301.be.engine.jwt.refresh.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.jwt.refresh.mapper.SysJwtRefreshMapper;
import com.cmcorg20230301.be.engine.jwt.refresh.model.dto.SysJwtRefreshSignInRefreshTokenDTO;
import com.cmcorg20230301.be.engine.jwt.refresh.model.entity.SysJwtRefreshDO;
import com.cmcorg20230301.be.engine.jwt.refresh.service.SysJwtRefreshService;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;

@Service
public class SysJwtRefreshServiceImpl extends ServiceImpl<SysJwtRefreshMapper, SysJwtRefreshDO>
    implements SysJwtRefreshService {

    /**
     * 通过：refreshToken登录
     */
    @Override
    public SignInVO signInRefreshToken(SysJwtRefreshSignInRefreshTokenDTO dto) {

        return null;

    }

}
