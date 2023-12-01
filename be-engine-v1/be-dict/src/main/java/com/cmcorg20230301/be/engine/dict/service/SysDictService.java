package com.cmcorg20230301.be.engine.dict.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictListByDictKeyDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysDictDO;

import java.util.List;

public interface SysDictService extends IService<SysDictDO> {

    String insertOrUpdate(SysDictInsertOrUpdateDTO dto);

    Page<SysDictDO> myPage(SysDictPageDTO dto);

    List<DictIntegerVO> listByDictKey(SysDictListByDictKeyDTO dto);

    List<SysDictDO> tree(SysDictPageDTO dto);

    SysDictDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet, boolean checkDeleteFlag);

    String addOrderNo(ChangeNumberDTO dto);

}
