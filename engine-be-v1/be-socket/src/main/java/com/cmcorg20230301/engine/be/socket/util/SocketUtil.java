package com.cmcorg20230301.engine.be.socket.util;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class SocketUtil {

    /**
     * 获取：ip
     */
    public static String getIp(Channel channel) {

        InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.remoteAddress();

        return inetSocketAddress.getAddress().getHostAddress();

    }

}
