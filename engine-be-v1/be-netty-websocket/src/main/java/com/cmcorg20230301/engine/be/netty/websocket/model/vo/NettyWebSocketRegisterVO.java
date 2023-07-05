package com.cmcorg20230301.engine.be.netty.websocket.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NettyWebSocketRegisterVO {

    @Schema(description = "webSocket 连接地址，scheme://ip:port/path?code=xxx")
    private String webSocketUrl;

}
