package com.cmcorg20230301.be.engine.jwt.refresh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.jwt.refresh.model.dto.SysJwtRefreshSignInRefreshTokenDTO;
import com.cmcorg20230301.be.engine.jwt.refresh.model.entity.SysJwtRefreshDO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;

public interface SysJwtRefreshService extends IService<SysJwtRefreshDO> {

    SignInVO signInRefreshToken(SysJwtRefreshSignInRefreshTokenDTO dto);

}
