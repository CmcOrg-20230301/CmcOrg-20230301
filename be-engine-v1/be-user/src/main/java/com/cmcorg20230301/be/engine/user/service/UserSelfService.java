package com.cmcorg20230301.be.engine.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.be.engine.user.model.vo.UserSelfInfoVO;

public interface UserSelfService extends IService<SysUserDO> {

    UserSelfInfoVO userSelfInfo();

    String userSelfUpdateInfo(UserSelfUpdateInfoDTO dto);

    String userSelfRefreshJwtSecretSuf();

    String userSelfResetAvatar();

}
