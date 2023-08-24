package com.cmcorg20230301.engine.be.area.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.area.model.dto.SysAreaPageDTO;
import com.cmcorg20230301.engine.be.area.model.entity.SysAreaDO;
import com.cmcorg20230301.engine.be.area.model.vo.SysAreaInfoByIdVO;
import com.cmcorg20230301.engine.be.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;

import java.util.List;

public interface SysAreaService extends IService<SysAreaDO> {

    String insertOrUpdate(SysAreaInsertOrUpdateDTO dto);

    Page<SysAreaDO> myPage(SysAreaPageDTO dto);

    List<SysAreaDO> tree(SysAreaPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysAreaInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(ChangeNumberDTO dto);

}
