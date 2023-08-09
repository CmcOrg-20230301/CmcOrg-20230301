package com.cmcorg20230301.engine.be.socket.util;

import cn.hutool.core.net.NetUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j(topic = LogTopicConstant.SOCKET)
public class SocketUtil {

    private static SysSocketService sysSocketService;

    @Resource
    public void setSysSocketService(SysSocketService sysSocketService) {
        SocketUtil.sysSocketService = sysSocketService;
    }

    /**
     * 获取：ip
     */
    public static String getIp(Channel channel) {

        InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.remoteAddress();

        return inetSocketAddress.getAddress().getHostAddress();

    }

    /**
     * 获取：ip
     */
    public static String getIp(FullHttpRequest fullHttpRequest) {

        String ip = "";

        for (String item : RequestUtil.IP_HEADER_ARR) {

            ip = fullHttpRequest.headers().get(item);

            if (NetUtil.isUnknown(ip) == false) {

                return NetUtil.getMultistageReverseProxyIp(ip);

            }

        }

        return ip;

    }

    /**
     * 关闭 socket
     *
     * @param disableFlag 是否是禁用，即：不删除数据库里面的数据
     */
    public static void closeSocket(ChannelFuture channelFuture, EventLoopGroup parentGroup, EventLoopGroup childGroup,
        Long sysSocketServerId, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Channel>> userIdChannelMap, String name,
        boolean disableFlag) {

        long closeChannelCount = 0;

        for (ConcurrentHashMap<Long, Channel> item : userIdChannelMap.values()) {

            for (Channel subItem : item.values()) {

                subItem.close();

                closeChannelCount++;

            }

        }

        boolean removeFlag = false;

        if (sysSocketServerId != null) {

            if (disableFlag) {

                removeFlag = true;

            } else {

                removeFlag = sysSocketService.removeById(sysSocketServerId);

            }

        }

        log.info("{} 下线{}：{}，移除连接：{}", name, removeFlag ? "成功" : "失败", sysSocketServerId, closeChannelCount);

        if (channelFuture != null) {

            channelFuture.channel().close().syncUninterruptibly();

        }

        if (parentGroup != null) {

            parentGroup.shutdownGracefully().syncUninterruptibly(); // 释放线程池资源

        }

        if (childGroup != null) {

            childGroup.shutdownGracefully().syncUninterruptibly(); // 释放线程池资源

        }

    }

}
