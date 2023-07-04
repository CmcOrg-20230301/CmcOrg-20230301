package com.cmcorg20230301.engine.be.netty.websocket.service;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullInteger;
import com.cmcorg20230301.engine.be.netty.websocket.model.vo.NettyWebSocketRegisterVO;

public interface NettyWebSocketService {

    NettyWebSocketRegisterVO register(NotNullInteger notNullInteger);

}
