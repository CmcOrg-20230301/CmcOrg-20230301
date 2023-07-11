package com.cmcorg20230301.engine.be.socket.util;

import cn.hutool.core.net.NetUtil;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.InetSocketAddress;

public class SocketUtil {

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

}
