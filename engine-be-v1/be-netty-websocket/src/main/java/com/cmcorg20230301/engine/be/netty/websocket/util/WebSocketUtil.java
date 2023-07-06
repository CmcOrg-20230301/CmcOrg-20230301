package com.cmcorg20230301.engine.be.netty.websocket.util;

import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.socket.model.dto.WebSocketMessageDTO;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketUtil {

    /**
     * 发送消息
     */
    public static <T> void send(Channel channel, WebSocketMessageDTO<T> dto) {

        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(dto)));

    }

}
