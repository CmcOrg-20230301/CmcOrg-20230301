package com.cmcorg20230301.be.engine.netty.tcp.protobuf.server;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.netty.tcp.protobuf.properties.NettyTcpProtobufProperties;
import com.cmcorg20230301.be.engine.security.util.TryUtil;

import cn.hutool.core.map.MapUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import protobuf.proto.BaseProto;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = LogTopicConstant.NETTY_TCP_PROTOBUF)
public class NettyTcpProtobufServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    NettyTcpProtobufProperties nettyTcpProtobufProperties;

    // userId key
    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("USER_ID_KEY");

    // sysSocketRefUserId key
    private static final AttributeKey<Long> SYS_SOCKET_REF_USER_ID_KEY =
        AttributeKey.valueOf("SYS_SOCKET_REF_USER_ID_KEY");

    // 用户通道 map，大key：用户主键 id，小key：sysSocketRefUserId，value：通道
    public static final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Channel>> USER_ID_CHANNEL_MAP =
        MapUtil.newConcurrentHashMap();

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

        TryUtil.tryCatchFinally(() -> {

            if (msg instanceof BaseProto.BaseRequest) {

                // 处理：BaseRequest
                handleBaseRequest((BaseProto.BaseRequest)msg);

            }

        }, () -> {

            ReferenceCountUtil.release(msg); // 备注：这里需要释放资源

        });

    }

    /**
     * 处理：BaseRequest
     */
    private void handleBaseRequest(@NotNull BaseProto.BaseRequest baseRequest) {

    }

}
