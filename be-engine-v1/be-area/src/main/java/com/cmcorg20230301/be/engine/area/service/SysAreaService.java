package com.cmcorg20230301.be.engine.area.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.area.model.dto.SysAreaPageDTO;
import com.cmcorg20230301.be.engine.area.model.vo.SysAreaInfoByIdVO;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.entity.SysAreaDO;

import java.util.List;

public interface SysAreaService extends IService<SysAreaDO> {

    String insertOrUpdate(SysAreaInsertOrUpdateDTO dto);

    Page<SysAreaDO> myPage(SysAreaPageDTO dto);

    List<SysAreaDO> tree(SysAreaPageDTO dto);

    SysAreaInfoByIdVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet, boolean checkChildrenFlag);

    String addOrderNo(ChangeNumberDTO dto);

}
