package com.cmcorg20230301.engine.be.netty.websocket.server;

import com.cmcorg20230301.engine.be.netty.websocket.configuration.NettyWebSocketBeanPostProcessor;
import com.cmcorg20230301.engine.be.netty.websocket.properties.NettyWebSocketProperties;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.MyThreadUtil;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
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
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@Slf4j
public class NettyWebSocketServer {

    @Resource
    SysSocketService sysSocketService;

    private Long sysSocketServerId = null; // 备注：启动完成之后，这个属性才有值

    public NettyWebSocketServer(NettyWebSocketProperties nettyWebSocketProperties,
        NettyWebSocketServerHandler nettyWebSocketServerHandler, MyThreadUtil myThreadUtil,
        SysSocketService sysSocketService) {

        if (nettyWebSocketProperties.getPort() == null) {

            log.info("NettyWebSocket 启动失败：未指定端口号");
            return;

        }

        MyThreadUtil.execute(() -> {

            // 启动
            start(nettyWebSocketProperties, nettyWebSocketServerHandler, nettyWebSocketProperties.getPort(),
                sysSocketService);

        });

    }

    @PreDestroy
    public void preDestroy() {

        if (sysSocketServerId != null) {

            sysSocketService.removeById(sysSocketServerId);

            log.info("NettyWebSocket 下线成功：{}", sysSocketServerId);

        }

    }

    /**
     * 启动
     */
    @SneakyThrows
    public void start(NettyWebSocketProperties nettyWebSocketProperties,
        NettyWebSocketServerHandler nettyWebSocketServerHandler, int port, SysSocketService sysSocketService) {

        EventLoopGroup childGroup = new NioEventLoopGroup();

        EventLoopGroup parentGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024); // 半连接池大小

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

                        ch.pipeline().addLast(nettyWebSocketServerHandler);

                        ch.pipeline().addLast(
                            new WebSocketServerProtocolHandler(nettyWebSocketProperties.getPath(), null, true,
                                65536 * 10));

                    }

                });

            ChannelFuture channelFuture = serverBootstrap.bind().sync(); // 服务器同步创建绑定

            SysSocketDO sysSocketDO = new SysSocketDO();

            sysSocketDO.setScheme(MyEntityUtil.getNotNullStr(nettyWebSocketProperties.getScheme()));
            sysSocketDO.setHost(MyEntityUtil.getNotNullStr(nettyWebSocketProperties.getHost()));
            sysSocketDO.setPort(port);
            sysSocketDO.setType(SysSocketTypeEnum.WEB_SOCKET);
            sysSocketDO.setEnableFlag(true);
            sysSocketDO.setDelFlag(false);
            sysSocketDO.setRemark("");

            sysSocketService.save(sysSocketDO);

            sysSocketServerId = sysSocketDO.getId();

            log.info("NettyWebSocket 启动完成：端口：{}，总接口个数：{}个", port, NettyWebSocketBeanPostProcessor.getMappingMapSize());

            channelFuture.channel().closeFuture().sync(); // 阻塞线程，监听关闭事件

        } finally {

            parentGroup.shutdownGracefully().sync(); // 释放线程池资源

            childGroup.shutdownGracefully().sync();

        }

    }

}
