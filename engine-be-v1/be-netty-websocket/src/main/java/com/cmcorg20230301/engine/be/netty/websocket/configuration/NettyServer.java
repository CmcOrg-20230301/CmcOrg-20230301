package com.cmcorg20230301.engine.be.netty.websocket.configuration;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.websocket.service.SysWebSocketService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * netty服务器
 */
@Component
@Slf4j
public class NettyServer implements CommandLineRunner {

    public static final String WS = "/ws";
    public static String ipAndPort = null; // ip:port
    public static String webSocketRegisterCodePreKey = null; // WebSocket 连接时的 redisKey前缀

    @Resource
    SysWebSocketService sysWebSocketService;

    @PreDestroy
    public void destroy() {

        sysWebSocketService.offlineAllForCurrent(); // WebSocket 全部下线

        log.info("WebSocket 离线成功");
    }

    @Override
    public void run(String... args) {

        int port = BaseConfiguration.port + 1; // WebSocket端口

        ipAndPort = BaseConfiguration.adminProperties.getWebSocketAddress() + ":" + port;

        webSocketRegisterCodePreKey = BaseRedisConstant.PRE_LOCK_WEB_SOCKET_REGISTER_CODE + ipAndPort + ":";

        sysWebSocketService.offlineAllForCurrent(); // WebSocket 全部下线

        ThreadUtil.execute(() -> start(port));

        log.info("WebSocket 启动完成：" + ipAndPort);
    }

    @SneakyThrows
    public void start(int port) {
        EventLoopGroup childGroup = new NioEventLoopGroup();
        EventLoopGroup parentGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(parentGroup, childGroup) // 绑定线程池
                .channel(NioServerSocketChannel.class) // 指定使用的channel
                .localAddress(port) // 绑定监听端口
                .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                    @Override
                    protected void initChannel(SocketChannel ch) { // 绑定客户端连接时候触发操作
                        // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                        ch.pipeline().addLast(new HttpServerCodec());
                        // 以块的方式来写的处理器
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(8192));
                        ch.pipeline().addLast(SpringUtil.getBean(MyNettyWebSocketHandler.class));
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler(WS, null, true, 65536 * 10));
                    }
                });
            ChannelFuture channelFuture = serverBootstrap.bind().sync(); // 服务器同步创建绑定
            channelFuture.channel().closeFuture().sync(); // 阻塞线程，监听关闭事件
        } finally {
            parentGroup.shutdownGracefully().sync(); // 释放线程池资源
            childGroup.shutdownGracefully().sync();
        }
    }

}
