package com.cmcorg20230301.engine.be.socket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.socket.model.dto.SysSocketPageDTO;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;

public interface SysSocketService extends IService<SysSocketDO> {

    Page<SysSocketDO> myPage(SysSocketPageDTO dto);

    SysSocketDO getSocketDOOfMinConnectNumber(SysSocketDO sysSocketDO);

}
