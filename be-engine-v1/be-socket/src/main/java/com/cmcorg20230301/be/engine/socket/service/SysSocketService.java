package com.cmcorg20230301.be.engine.socket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.socket.model.dto.SysSocketPageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketDO;

public interface SysSocketService extends IService<SysSocketDO> {

    Page<SysSocketDO> myPage(SysSocketPageDTO dto);

    String disableByIdSet(NotEmptyIdSet notEmptyIdSet);

    String enableByIdSet(NotEmptyIdSet notEmptyIdSet);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
