package com.cmcorg20230301.engine.be.netty.websocket.server;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.netty.websocket.properties.NettyWebSocketProperties;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = LogTopicConstant.NETTY_WEB_SOCKET)
public class NettyWebSocketServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    NettyWebSocketProperties nettyWebSocketProperties;

    // 用户通道 map，key：用户主键 id，value：通道集合
    private static final Map<Long, Set<Channel>> USER_ID_CHANNEL_MAP = MapUtil.newConcurrentHashMap();

    // userId key
    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf(MyJwtUtil.PAYLOAD_MAP_USER_ID_KEY);

    /**
     * 连接成功时
     */
    @SneakyThrows
    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {

        super.channelActive(ctx);

    }

    /**
     * 调用 close等操作，连接断开时
     */
    @SneakyThrows
    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {

        super.channelInactive(ctx);

    }

    /**
     * 发生异常时，比如：远程主机强迫关闭了一个现有的连接
     */
    @SneakyThrows
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

        ctx.close(); // 会执行：channelInactive 方法

        super.exceptionCaught(ctx, e);

    }

    /**
     * 收到消息时
     */
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {

        try {

            // 首次连接是 FullHttpRequest，处理参数
            if (msg instanceof FullHttpRequest) {

                FullHttpRequest fullHttpRequest = (FullHttpRequest)msg;

                UrlQuery urlQuery = UrlQuery.of(fullHttpRequest.uri(), CharsetUtil.CHARSET_UTF_8);

                String code = Convert.toStr(urlQuery.get("code")); // 随机码

                if (StrUtil.isBlank(code)) {
                    ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
                }

                // url包含参数，需要舍弃
                fullHttpRequest.setUri(nettyWebSocketProperties.getPath());

            } else if (msg instanceof TextWebSocketFrame) {

                // 读取消息
                TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)msg;

            }

        } finally {

            ReferenceCountUtil.release(msg); // 释放资源

        }

    }

}
