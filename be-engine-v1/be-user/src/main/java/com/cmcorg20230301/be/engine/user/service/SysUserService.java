package com.cmcorg20230301.be.engine.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserDictListDTO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserPageDTO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserUpdatePasswordDTO;
import com.cmcorg20230301.be.engine.user.model.vo.SysUserInfoByIdVO;
import com.cmcorg20230301.be.engine.user.model.vo.SysUserPageVO;

public interface SysUserService extends IService<SysUserDO> {

    Page<SysUserPageVO> myPage(SysUserPageDTO dto);

    Page<DictVO> dictList(SysUserDictListDTO dto);

    String insertOrUpdate(SysUserInsertOrUpdateDTO dto);

    SysUserInfoByIdVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet);

    String resetAvatar(NotEmptyIdSet notEmptyIdSet);

    String updatePassword(SysUserUpdatePasswordDTO dto);

    String thaw(NotEmptyIdSet notEmptyIdSet);

    String freeze(NotEmptyIdSet notEmptyIdSet);

}
