package com.cmcorg20230301.engine.be.netty.tcp.protobuf.server;

import com.cmcorg20230301.engine.be.netty.tcp.protobuf.configuration.NettyTcpProtobufBeanPostProcessor;
import com.cmcorg20230301.engine.be.netty.tcp.protobuf.properties.NettyTcpProtobufProperties;
import com.cmcorg20230301.engine.be.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.engine.be.security.configuration.base.BaseConfiguration;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import com.cmcorg20230301.engine.be.security.util.MyThreadUtil;
import com.cmcorg20230301.engine.be.socket.mapper.SysSocketRefUserMapper;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import protobuf.proto.BaseProto;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NettyTcpProtobufServer {

    @Resource
    NettyTcpProtobufProperties nettyTcpProtobufProperties;

    @Resource
    NettyTcpProtobufServerHandler nettyTcpProtobufServerHandler;

    @Resource
    MyThreadUtil myThreadUtil;

    @Resource
    SysSocketService sysSocketService;

    @Resource
    BaseConfiguration baseConfiguration;

    @Resource
    IdGeneratorUtil idGeneratorUtil;

    @Resource
    SysSocketRefUserMapper sysSocketRefUserMapper;

    private Long sysSocketServerId = null; // 备注：启动完成之后，这个属性才有值

    @PostConstruct
    public void postConstruct() {

        int port = BaseConfiguration.port + 2;

        MyThreadUtil.execute(() -> {

            // 启动
            start(nettyTcpProtobufProperties, nettyTcpProtobufServerHandler, port, sysSocketService);

        });

    }

    @PreDestroy
    public void preDestroy() {

        if (sysSocketServerId != null) {

            long closeChannelCount = 0;

            for (ConcurrentHashMap<Long, Channel> item : NettyTcpProtobufServerHandler.USER_ID_CHANNEL_MAP.values()) {

                for (Channel subItem : item.values()) {

                    subItem.close();

                    closeChannelCount++;

                }

            }

            boolean removeFlag = sysSocketService.removeById(sysSocketServerId);

            log.info("NettyTcpProtobuf 下线{}：{}，移除连接：{}", removeFlag ? "成功" : "失败", sysSocketServerId,
                closeChannelCount);

        }

    }

    /**
     * 启动
     */
    @SneakyThrows
    public void start(NettyTcpProtobufProperties nettyTcpProtobufProperties,
        NettyTcpProtobufServerHandler nettyTcpProtobufServerHandler, int port, SysSocketService sysSocketService) {

        EventLoopGroup parentGroup = new NioEventLoopGroup(nettyTcpProtobufProperties.getParentSize());

        EventLoopGroup childGroup = new NioEventLoopGroup(nettyTcpProtobufProperties.getParentSize());

        try {

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

            ChannelFuture channelFuture = serverBootstrap.bind().sync(); // 服务器同步创建绑定

            SysSocketDO sysSocketDO = new SysSocketDO();

            sysSocketDO.setScheme(MyEntityUtil.getNotNullStr(nettyTcpProtobufProperties.getScheme()));
            sysSocketDO.setHost(MyEntityUtil.getNotNullStr(nettyTcpProtobufProperties.getHost()));
            sysSocketDO.setPort(port);
            sysSocketDO.setPath(MyEntityUtil.getNotNullStr(nettyTcpProtobufProperties.getPath()));
            sysSocketDO.setType(SysSocketTypeEnum.TCP_PROTOBUF);
            sysSocketDO.setEnableFlag(true);
            sysSocketDO.setDelFlag(false);
            sysSocketDO.setRemark("");

            sysSocketService.save(sysSocketDO);

            sysSocketServerId = sysSocketDO.getId();

            log.info("NettyTcpProtobuf 启动完成：端口：{}，总接口个数：{}个", port,
                NettyTcpProtobufBeanPostProcessor.getMappingMapSize());

            channelFuture.channel().closeFuture().sync(); // 阻塞线程，监听关闭事件

        } finally {

            parentGroup.shutdownGracefully().sync(); // 释放线程池资源

            childGroup.shutdownGracefully().sync();

        }

    }

}
