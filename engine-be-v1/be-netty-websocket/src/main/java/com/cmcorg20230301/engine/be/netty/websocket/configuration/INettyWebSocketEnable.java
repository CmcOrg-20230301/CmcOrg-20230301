package com.cmcorg20230301.engine.be.netty.websocket.configuration;

import com.cmcorg20230301.engine.be.netty.websocket.server.NettyWebSocketServer;
import com.cmcorg20230301.engine.be.socket.model.configuration.ISocketEnable;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class INettyWebSocketEnable implements ISocketEnable {

    @Override
    public void handle(Set<Long> socketIdSet) {

        if (socketIdSet.contains(NettyWebSocketServer.sysSocketServerId)) {

            // 关闭 webSocket
            NettyWebSocketServer.restart();

        }

    }

}
