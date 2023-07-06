package com.cmcorg20230301.engine.be.netty.websocket.util;

import com.cmcorg20230301.engine.be.socket.model.dto.WebSocketMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class WebSocketUtil {

    private static ObjectMapper objectMapper;

    @Resource
    public void setObjectMapper(ObjectMapper objectMapper) {
        WebSocketUtil.objectMapper = objectMapper;
    }

    /**
     * 发送消息
     */
    @SneakyThrows
    public static <T> void send(Channel channel, WebSocketMessageDTO<T> dto) {

        channel.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(dto)));

    }

}
