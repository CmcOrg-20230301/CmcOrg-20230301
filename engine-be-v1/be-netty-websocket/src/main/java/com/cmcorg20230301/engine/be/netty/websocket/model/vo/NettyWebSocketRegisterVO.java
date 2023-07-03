package com.cmcorg20230301.engine.be.netty.websocket.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NettyWebSocketRegisterVO {

    @Schema(description = "webSocket 连接地址，ip:port")
    private String webSocketUrl;

    @Schema(description = "webSocket 连接码，备注：只能使用一次")
    private String code;

}
