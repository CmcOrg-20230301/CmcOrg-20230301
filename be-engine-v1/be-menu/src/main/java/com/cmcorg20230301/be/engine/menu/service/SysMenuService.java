package com.cmcorg20230301.be.engine.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.menu.model.dto.SysMenuInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.menu.model.dto.SysMenuPageDTO;
import com.cmcorg20230301.be.engine.menu.model.vo.SysMenuInfoByIdVO;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.entity.SysMenuDO;

import java.util.List;

public interface SysMenuService extends IService<SysMenuDO> {

    String insertOrUpdate(SysMenuInsertOrUpdateDTO dto);

    Page<SysMenuDO> myPage(SysMenuPageDTO dto);

    List<SysMenuDO> tree(SysMenuPageDTO dto);

    SysMenuInfoByIdVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet, boolean checkChildrenFlag);

    List<SysMenuDO> userSelfMenuList();

    String addOrderNo(ChangeNumberDTO dto);

}
