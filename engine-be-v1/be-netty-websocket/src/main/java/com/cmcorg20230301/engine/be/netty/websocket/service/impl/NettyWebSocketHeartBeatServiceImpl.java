package com.cmcorg20230301.engine.be.netty.websocket.service.impl;

import com.cmcorg20230301.engine.be.netty.websocket.server.NettyWebSocketServer;
import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketHeartBeatService;
import org.springframework.stereotype.Service;

@Service
public class NettyWebSocketHeartBeatServiceImpl implements NettyWebSocketHeartBeatService {

    /**
     * 心跳检测
     */
    @Override
    public Long heartBeat() {
        return NettyWebSocketServer.sysSocketServerId;
    }

}
