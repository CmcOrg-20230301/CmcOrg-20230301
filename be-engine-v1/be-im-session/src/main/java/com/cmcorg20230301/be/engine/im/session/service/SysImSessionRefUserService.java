package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionRefUserJoinUserIdSetDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionRefUserQueryRefUserInfoMapVO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndLongSet;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;

public interface SysImSessionRefUserService extends IService<SysImSessionRefUserDO> {

    String joinUserIdSet(SysImSessionRefUserJoinUserIdSetDTO dto);

    LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO>
        queryRefUserInfoMap(NotNullIdAndLongSet notNullIdAndLongSet);

    String updateLastOpenTsUserSelf(NotNullId notNullId);

}
