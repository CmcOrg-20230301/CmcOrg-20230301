package com.cmcorg20230301.be.engine.dept.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.dept.model.dto.SysDeptInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.dept.model.dto.SysDeptPageDTO;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.entity.SysDeptDO;

import java.util.List;

public interface SysDeptService extends IService<SysDeptDO> {

    String insertOrUpdate(SysDeptInsertOrUpdateDTO dto);

    Page<SysDeptDO> myPage(SysDeptPageDTO dto);

    List<SysDeptDO> tree(SysDeptPageDTO dto);

    SysDeptDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet, boolean checkChildrenFlag);

    String addOrderNo(ChangeNumberDTO dto);

}
