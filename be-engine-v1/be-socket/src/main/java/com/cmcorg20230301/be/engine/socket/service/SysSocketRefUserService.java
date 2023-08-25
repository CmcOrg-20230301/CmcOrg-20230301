package com.cmcorg20230301.be.engine.socket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.socket.model.dto.SysSocketRefUserPageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketRefUserDO;

public interface SysSocketRefUserService extends IService<SysSocketRefUserDO> {

    Page<SysSocketRefUserDO> myPage(SysSocketRefUserPageDTO dto);

    String offlineByIdSet(NotEmptyIdSet notEmptyIdSet);

}
