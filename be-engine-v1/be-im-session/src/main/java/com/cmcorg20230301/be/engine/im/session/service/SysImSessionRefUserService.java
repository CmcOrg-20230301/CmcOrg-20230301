package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionRefUserSelfPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionRefUserQueryRefUserInfoMapVO;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndLongSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;

public interface SysImSessionRefUserService extends IService<SysImSessionRefUserDO> {

    String joinUserIdSet(NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet);

    LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO> queryRefUserInfoMap(NotNullIdAndLongSet notNullIdAndLongSet);

    String updateLastOpenTsUserSelf(NotNullId notNullId);

    Page<SysImSessionDO> myPageSelf(SysImSessionRefUserSelfPageDTO dto);

}
