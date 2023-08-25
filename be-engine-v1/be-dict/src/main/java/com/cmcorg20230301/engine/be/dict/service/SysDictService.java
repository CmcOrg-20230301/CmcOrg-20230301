package com.cmcorg20230301.engine.be.dict.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictListByDictKeyDTO;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.engine.be.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.model.model.vo.DictVO;
import com.cmcorg20230301.engine.be.security.model.entity.SysDictDO;

import java.util.List;

public interface SysDictService extends IService<SysDictDO> {

    String insertOrUpdate(SysDictInsertOrUpdateDTO dto);

    Page<SysDictDO> myPage(SysDictPageDTO dto);

    List<DictVO> listByDictKey(SysDictListByDictKeyDTO dto);

    List<SysDictDO> tree(SysDictPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysDictDO infoById(NotNullId notNullId);

    String addOrderNo(ChangeNumberDTO dto);

}
