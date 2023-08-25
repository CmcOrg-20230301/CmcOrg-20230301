package com.cmcorg20230301.be.engine.netty.websocket.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndIntegerValue;

import java.util.Set;

public interface NettyWebSocketService {

    Set<String> getAllWebSocketUrl();

    String getWebSocketUrlById(NotNullIdAndIntegerValue notNullIdAndIntegerValue);

}
