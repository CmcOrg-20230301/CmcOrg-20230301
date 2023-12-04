package com.cmcorg20230301.be.engine.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.*;
import com.cmcorg20230301.be.engine.model.model.vo.DictTreeVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysMenuDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.tenant.model.dto.SysTenantPageDTO;
import com.cmcorg20230301.be.engine.tenant.model.vo.SysTenantInfoByIdVO;

import java.util.List;

public interface SysTenantService extends IService<SysTenantDO> {

    String insertOrUpdate(SysTenantInsertOrUpdateDTO dto);

    Page<SysTenantDO> myPage(SysTenantPageDTO dto);

    Page<DictTreeVO> dictList();

    List<SysTenantDO> tree(SysTenantPageDTO dto);

    SysTenantInfoByIdVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    String addOrderNo(ChangeNumberDTO dto);

    String getNameById(NotNullLong notNullLong);

    List<SysMenuDO> getSyncMenuInfo(NotNullId notEmptyIdSet);

    String doSyncMenu(NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet);

    String deleteTenantAllMenu(NotEmptyIdSet notEmptyIdSet);

    String doSyncDict();

    String doSyncParam();

    String thaw(NotEmptyIdSet notEmptyIdSet);

    String freeze(NotEmptyIdSet notEmptyIdSet);

}
