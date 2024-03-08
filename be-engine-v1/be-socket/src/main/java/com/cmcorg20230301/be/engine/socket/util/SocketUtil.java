package com.cmcorg20230301.be.engine.socket.util;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.properties.SysSocketBaseProperties;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.RequestUtil;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.be.engine.socket.service.SysSocketRefUserService;
import com.cmcorg20230301.be.engine.socket.service.SysSocketService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j(topic = LogTopicConstant.SOCKET)
public class SocketUtil {

    private static SysSocketService sysSocketService;

    @Resource
    public void setSysSocketService(SysSocketService sysSocketService) {
        SocketUtil.sysSocketService = sysSocketService;
    }

    private static SysSocketRefUserService sysSocketRefUserService;

    @Resource
    public void setSysSocketService(SysSocketRefUserService sysSocketRefUserService) {
        SocketUtil.sysSocketRefUserService = sysSocketRefUserService;
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
    public static String getIp(FullHttpRequest fullHttpRequest, Channel channel) {

        String ip = "";

        for (String item : RequestUtil.IP_HEADER_ARR) {

            ip = fullHttpRequest.headers().get(item);

            if (NetUtil.isUnknown(ip) == false) {

                return NetUtil.getMultistageReverseProxyIp(ip);

            }

        }

        if (StrUtil.isBlank(ip)) {

            ip = getIp(channel);

        }

        if (StrUtil.isBlank(ip)) {

            ip = "";

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

    /**
     * 获取：sysSocketServerId
     */
    public static Long getSysSocketServerId(int port, SysSocketBaseProperties sysSocketBaseProperties,
        SysSocketTypeEnum sysSocketTypeEnum) {

        SysSocketDO sysSocketDO = new SysSocketDO();

        sysSocketDO.setScheme(MyEntityUtil.getNotNullStr(sysSocketBaseProperties.getScheme()));
        sysSocketDO.setHost(MyEntityUtil.getNotNullStr(sysSocketBaseProperties.getHost()));
        sysSocketDO.setPort(port);
        sysSocketDO.setPath(MyEntityUtil.getNotNullStr(sysSocketBaseProperties.getPath()));
        sysSocketDO.setType(sysSocketTypeEnum);

        sysSocketDO.setMacAddress(NetUtil.getLocalMacAddress());

        sysSocketDO.setEnableFlag(true);
        sysSocketDO.setDelFlag(false);
        sysSocketDO.setRemark("");

        // 移除：mac地址，port，相同的 socket数据
        List<SysSocketDO> sysSocketDOList =
            sysSocketService.lambdaQuery().eq(SysSocketDO::getMacAddress, sysSocketDO.getMacAddress())
                .eq(SysSocketDO::getPort, sysSocketDO.getPort()).select(BaseEntity::getId).list();

        if (CollUtil.isNotEmpty(sysSocketDOList)) {

            Set<Long> socketIdSet = sysSocketDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

            sysSocketRefUserService.lambdaUpdate().in(SysSocketRefUserDO::getSocketId, socketIdSet).remove();

            sysSocketService.removeBatchByIds(socketIdSet);

        }

        sysSocketService.save(sysSocketDO);

        return sysSocketDO.getId();

    }

}
