package com.cmcorg20230301.be.engine.pay.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationPageDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;

public interface SysPayConfigurationService extends IService<SysPayConfigurationDO> {

    String insertOrUpdate(SysPayConfigurationInsertOrUpdateDTO dto);

    Page<SysPayConfigurationDO> myPage(SysPayConfigurationPageDTO dto);

    Page<DictVO> dictList();

    SysPayConfigurationDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
