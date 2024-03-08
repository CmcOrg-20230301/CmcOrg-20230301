package com.cmcorg20230301.be.engine.netty.tcp.protobuf.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.netty.tcp.protobuf.configuration.NettyTcpProtobufBeanPostProcessor;
import com.cmcorg20230301.be.engine.netty.tcp.protobuf.properties.NettyTcpProtobufProperties;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.socket.mapper.SysSocketRefUserMapper;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.be.engine.socket.service.SysSocketService;
import com.cmcorg20230301.be.engine.socket.util.SocketUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import protobuf.proto.BaseProto;

@Component
@Slf4j
public class NettyTcpProtobufServer {

    private static NettyTcpProtobufProperties nettyTcpProtobufProperties;

    @Resource
    public void setNettyWebSocketProperties(NettyTcpProtobufProperties nettyTcpProtobufProperties) {
        NettyTcpProtobufServer.nettyTcpProtobufProperties = nettyTcpProtobufProperties;
    }

    private static NettyTcpProtobufServerHandler nettyTcpProtobufServerHandler;

    @Resource
    public void setNettyWebSocketServerHandler(NettyTcpProtobufServerHandler nettyTcpProtobufServerHandler) {
        NettyTcpProtobufServer.nettyTcpProtobufServerHandler = nettyTcpProtobufServerHandler;
    }

    private static MyThreadUtil myThreadUtil;

    @Resource
    public void setMyThreadUtil(MyThreadUtil myThreadUtil) {
        NettyTcpProtobufServer.myThreadUtil = myThreadUtil;
    }

    private static SysSocketService sysSocketService;

    @Resource
    public void setSysSocketService(SysSocketService sysSocketService) {
        NettyTcpProtobufServer.sysSocketService = sysSocketService;
    }

    private static BaseConfiguration baseConfiguration;

    @Resource
    public void setBaseConfiguration(BaseConfiguration baseConfiguration) {
        NettyTcpProtobufServer.baseConfiguration = baseConfiguration;
    }

    private static IdGeneratorUtil idGeneratorUtil;

    @Resource
    public void setIdGeneratorUtil(IdGeneratorUtil idGeneratorUtil) {
        NettyTcpProtobufServer.idGeneratorUtil = idGeneratorUtil;
    }

    private static SysSocketRefUserMapper sysSocketRefUserMapper;

    @Resource
    public void setSysSocketRefUserMapper(SysSocketRefUserMapper sysSocketRefUserMapper) {
        NettyTcpProtobufServer.sysSocketRefUserMapper = sysSocketRefUserMapper;
    }

    private static SocketUtil socketUtil;

    @Resource
    public void setSocketUtil(SocketUtil socketUtil) {
        NettyTcpProtobufServer.socketUtil = socketUtil;
    }

    public static Long sysSocketServerId = null; // 备注：启动完成之后，这个属性才有值

    private static ChannelFuture channelFuture = null; // 备注：启动完成之后，这个属性才有值

    private static EventLoopGroup parentGroup = null; // 备注：启动完成之后，这个属性才有值

    private static EventLoopGroup childGroup = null; // 备注：启动完成之后，这个属性才有值

    /**
     * 重启 socket
     */
    public synchronized static void restart() {

        stop(false); // 关闭 socket

        start(); // 启动 socket

    }

    /**
     * 关闭 socket
     */
    public synchronized static void stop(boolean disableFlag) {

        // 关闭 socket
        SocketUtil.closeSocket(channelFuture, parentGroup, childGroup, sysSocketServerId,
            NettyTcpProtobufServerHandler.USER_ID_CHANNEL_MAP, "NettyTcpProtobuf", disableFlag);

        if (!disableFlag) {

            sysSocketServerId = null;

        }

        channelFuture = null;
        parentGroup = null;
        childGroup = null;

    }

    @PostConstruct
    public void postConstruct() {

        // 启动 socket
        start();

    }

    @PreDestroy
    public void preDestroy() {

        // 关闭 socket
        stop(false);

    }

    /**
     * 启动 socket
     */
    @SneakyThrows
    private synchronized static void start() {

        if (sysSocketServerId != null) {
            return;
        }

        int port = BaseConfiguration.port + 2;

        parentGroup = new NioEventLoopGroup(nettyTcpProtobufProperties.getParentSize());

        childGroup = new NioEventLoopGroup(nettyTcpProtobufProperties.getParentSize());

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024); // 半连接池大小
        serverBootstrap.option(ChannelOption.SO_REUSEADDR, true); // 允许重复使用本地地址和端口
        serverBootstrap.childOption(ChannelOption.ALLOW_HALF_CLOSURE, false); // 一个连接的远端关闭时，本地端自动关闭

        serverBootstrap.group(parentGroup, childGroup) // 绑定线程池

            .channel(NioServerSocketChannel.class) // 指定使用的channel

            .localAddress(port) // 绑定监听端口

            .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                @Override
                protected void initChannel(SocketChannel ch) { // 绑定客户端连接时候触发操作

                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());

                    ch.pipeline().addLast(new ProtobufDecoder(BaseProto.BaseRequest.getDefaultInstance()));

                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());

                    ch.pipeline().addLast(new ProtobufEncoder());

                    ch.pipeline().addLast(nettyTcpProtobufServerHandler);

                }

            });

        channelFuture = serverBootstrap.bind().sync(); // 服务器同步创建绑定

        sysSocketServerId =
            SocketUtil.getSysSocketServerId(port, nettyTcpProtobufProperties, SysSocketTypeEnum.TCP_PROTOBUF);

        log.info("NettyTcpProtobuf 启动完成：端口：{}，总接口个数：{}个", port, NettyTcpProtobufBeanPostProcessor.getMappingMapSize());

    }

}
