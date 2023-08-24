package com.cmcorg20230301.engine.be.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.role.model.dto.SysRolePageDTO;
import com.cmcorg20230301.engine.be.role.model.vo.SysRoleInfoByIdVO;
import com.cmcorg20230301.engine.be.security.model.entity.SysRoleDO;

public interface SysRoleService extends IService<SysRoleDO> {

    String insertOrUpdate(SysRoleInsertOrUpdateDTO dto);

    Page<SysRoleDO> myPage(SysRolePageDTO dto);

    SysRoleInfoByIdVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
