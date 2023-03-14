package com.cmcorg20230301.engine.be.netty.websocket.util;

import com.admin.common.model.enums.WebSocketMessageEnum;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class MyWebSocketUtil {

    public static TextWebSocketFrame getTextWebSocketFrame(WebSocketMessageEnum webSocketMessageEnum) {
        return new TextWebSocketFrame(webSocketMessageEnum.toJsonString());
    }

}
