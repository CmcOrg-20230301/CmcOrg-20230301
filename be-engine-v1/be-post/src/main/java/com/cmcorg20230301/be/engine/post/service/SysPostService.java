package com.cmcorg20230301.be.engine.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.post.model.dto.SysPostInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.post.model.dto.SysPostPageDTO;
import com.cmcorg20230301.be.engine.post.model.entity.SysPostDO;
import com.cmcorg20230301.be.engine.post.model.vo.SysPostInfoByIdVO;

import java.util.List;

public interface SysPostService extends IService<SysPostDO> {

    String insertOrUpdate(SysPostInsertOrUpdateDTO dto);

    Page<SysPostDO> myPage(SysPostPageDTO dto);

    List<SysPostDO> tree(SysPostPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysPostInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(ChangeNumberDTO dto);

}
