package com.cmcorg20230301.engine.be.netty.websocket.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.websocket.model.dto.SysWebSocketPageDTO;
import com.admin.websocket.model.entity.SysWebSocketDO;
import com.admin.websocket.model.vo.SysWebSocketRegisterVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface SysWebSocketService extends IService<SysWebSocketDO> {

    void offlineAllForCurrent();

    String retreatAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet);

    String retreatAndNoticeAll();

    SysWebSocketRegisterVO register(NotNullByte notNullByte);

    Page<SysWebSocketDO> myPage(SysWebSocketPageDTO dto);

    String changeType(NotNullByteAndId notNullByteAndId);

    void offlineByWebSocketIdSet(Set<Long> webSocketIdSet);

    void offlineByUserIdSet(Set<Long> userIdSet);
}
