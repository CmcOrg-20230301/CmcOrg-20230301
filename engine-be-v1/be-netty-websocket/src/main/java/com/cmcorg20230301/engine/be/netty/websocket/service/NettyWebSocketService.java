package com.cmcorg20230301.engine.be.netty.websocket.service;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullInteger;

import java.util.Set;

public interface NettyWebSocketService {

    Set<String> getAllWebSocketUrl(NotNullInteger notNullInteger);

}
