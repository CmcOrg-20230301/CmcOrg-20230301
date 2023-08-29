package com.cmcorg20230301.be.engine.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantPageDTO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantInfoByIdVO;

import java.util.List;

public interface SysTenantService extends IService<SysTenantDO> {

    String insertOrUpdate(SysTenantInsertOrUpdateDTO dto);

    Page<SysTenantDO> myPage(SysTenantPageDTO dto);

    Page<DictVO> dictList();

    List<SysTenantDO> tree(SysTenantPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysTenantInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(ChangeNumberDTO dto);

    String getNameById(NotNullId notNullId);

}
