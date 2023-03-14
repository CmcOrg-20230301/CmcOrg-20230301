package com.cmcorg20230301.engine.be.netty.websocket.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysWebSocketRegisterVO {

    @ApiModelProperty(value = "WebSocket 连接地址，ip:port")
    private String webSocketUrl;

    @ApiModelProperty(value = "WebSocket 连接码，备注：只能使用一次")
    private String code;

}
