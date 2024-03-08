package com.cmcorg20230301.be.engine.netty.websocket.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.netty.websocket.configuration.NettyWebSocketBeanPostProcessor;
import com.cmcorg20230301.be.engine.netty.websocket.server.NettyWebSocketServerHandler;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.util.RequestUtil;
import com.cmcorg20230301.be.engine.security.util.SysUserInfoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;

@Component
public class WebSocketUtil {

    // 目的：Long 转 String，Enum 转 code
    private static ObjectMapper objectMapper;

    @Resource
    public void setObjectMapper(ObjectMapper objectMapper) {
        WebSocketUtil.objectMapper = objectMapper;
    }

    /**
     * 发送消息
     */
    @SneakyThrows
    public static void send(@Nullable SysWebSocketEventBO<?> bo) {

        if (bo == null) {
            return;
        }

        Set<Long> userIdSet = bo.getUserIdSet();

        if (CollUtil.isEmpty(userIdSet) || bo.getWebSocketMessageDTO() == null) {
            return;
        }

        String jsonStr = objectMapper.writeValueAsString(bo.getWebSocketMessageDTO());

        Set<Long> sysSocketRefUserIdSet = bo.getSysSocketRefUserIdSet();

        boolean checkFlag = CollUtil.isNotEmpty(sysSocketRefUserIdSet);

        for (Long item : userIdSet) {

            ConcurrentHashMap<Long, Channel> channelMap = NettyWebSocketServerHandler.USER_ID_CHANNEL_MAP.get(item);

            if (CollUtil.isEmpty(channelMap)) {
                continue;
            }

            // 再包一层：防止遍历的时候，集合被修改
            List<Channel> channelList = new ArrayList<>(channelMap.values());

            for (Channel subItem : channelList) {

                if (checkFlag) {

                    Long sysSocketRefUserId =
                        subItem.attr(NettyWebSocketServerHandler.SYS_SOCKET_REF_USER_ID_KEY).get();

                    if (!sysSocketRefUserIdSet.contains(sysSocketRefUserId)) {
                        continue;
                    }

                }

                // 发送数据
                subItem.writeAndFlush(new TextWebSocketFrame(jsonStr));

            }

        }

    }

    /**
     * 发送消息
     */
    @SneakyThrows
    public static <T> void send(Channel channel, WebSocketMessageDTO<T> dto, String text, long costMs,
        @Nullable NettyWebSocketBeanPostProcessor.MappingValue mappingValue, String errorMsg, boolean successFlag) {

        Long userId = channel.attr(NettyWebSocketServerHandler.USER_ID_KEY).get();

        Long tenantId = channel.attr(NettyWebSocketServerHandler.TENANT_ID_KEY).get();

        Date date = new Date();

        costMs = System.currentTimeMillis() - costMs; // 耗时（毫秒）
        String costMsStr = DateUtil.formatBetween(costMs, BetweenFormatter.Level.MILLISECOND); // 耗时（字符串）

        String summary;

        if (mappingValue == null) {

            summary = "";

        } else {

            Operation operation = mappingValue.getMethod().getAnnotation(Operation.class);

            summary = operation.summary();

        }

        String jsonStr = objectMapper.writeValueAsString(dto);

        SysRequestDO sysRequestDO = new SysRequestDO();

        sysRequestDO.setUri(dto.getUri());
        sysRequestDO.setCostMsStr(costMsStr);
        sysRequestDO.setCostMs(costMs);
        sysRequestDO.setName(summary);
        sysRequestDO.setCategory(channel.attr(NettyWebSocketServerHandler.SYS_REQUEST_CATEGORY_ENUM_KEY).get());

        String ip = channel.attr(NettyWebSocketServerHandler.IP_KEY).get();

        sysRequestDO.setIp(ip);
        sysRequestDO.setRegion(Ip2RegionUtil.getRegion(sysRequestDO.getIp()));

        // 更新：用户信息
        SysUserInfoUtil.add(userId, date, sysRequestDO.getIp(), sysRequestDO.getRegion());

        sysRequestDO.setSuccessFlag(successFlag);
        sysRequestDO.setErrorMsg(errorMsg);
        sysRequestDO.setRequestParam(text);
        sysRequestDO.setType(OperationDescriptionConstant.WEB_SOCKET);
        sysRequestDO.setResponseValue(jsonStr);

        sysRequestDO.setTenantId(tenantId);

        sysRequestDO.setCreateId(userId);
        sysRequestDO.setCreateTime(date);
        sysRequestDO.setUpdateId(userId);
        sysRequestDO.setUpdateTime(date);

        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);
        sysRequestDO.setRemark("");

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

        // 发送数据
        channel.writeAndFlush(new TextWebSocketFrame(jsonStr));

    }

}
