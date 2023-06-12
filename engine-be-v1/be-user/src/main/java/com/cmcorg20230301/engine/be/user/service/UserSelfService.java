package com.cmcorg20230301.engine.be.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.user.model.dto.UserSelfUpdateInfoDTO;
import com.cmcorg20230301.engine.be.user.model.vo.UserSelfInfoVO;

public interface UserSelfService extends IService<SysUserDO> {

    UserSelfInfoVO userSelfInfo();

    String userSelfUpdateInfo(UserSelfUpdateInfoDTO dto);

    String userSelfRefreshJwtSecretSuf();

    String userSelfResetAvatar();

}
