package com.cmcorg20230301.engine.be.netty.websocket.websocket;

import com.cmcorg20230301.engine.be.netty.websocket.annotation.NettyWebSocketController;
import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketHeartBeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@NettyWebSocketController
@RequestMapping(value = "/netty/webSocket/heartBeat")
@Tag(name = "WebSocket-心跳检测")
public class NettyWebSocketHeartBeatController {

    @Resource
    NettyWebSocketHeartBeatService baseService;

    @Operation(summary = "心跳检测")
    @PostMapping
    public Long heartBeat() {
        return baseService.heartBeat();
    }

}
