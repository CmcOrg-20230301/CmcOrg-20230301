package com.cmcorg20230301.engine.be.netty.websocket.server;

import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = LogTopicConstant.NETTY_WEB_SOCKET)
public class NettyWebSocketServerHandler extends ChannelInboundHandlerAdapter {

}
