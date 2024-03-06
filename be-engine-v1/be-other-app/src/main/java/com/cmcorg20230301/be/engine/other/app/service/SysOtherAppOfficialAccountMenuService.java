package com.cmcorg20230301.be.engine.other.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountMenuInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountMenuPageDTO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppOfficialAccountMenuDO;
import java.util.List;

public interface SysOtherAppOfficialAccountMenuService extends
    IService<SysOtherAppOfficialAccountMenuDO> {

    String insertOrUpdate(SysOtherAppOfficialAccountMenuInsertOrUpdateDTO dto);

    Page<SysOtherAppOfficialAccountMenuDO> myPage(SysOtherAppOfficialAccountMenuPageDTO dto);

    List<SysOtherAppOfficialAccountMenuDO> tree(SysOtherAppOfficialAccountMenuPageDTO dto);

    SysOtherAppOfficialAccountMenuDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    String addOrderNo(ChangeNumberDTO dto);

}
