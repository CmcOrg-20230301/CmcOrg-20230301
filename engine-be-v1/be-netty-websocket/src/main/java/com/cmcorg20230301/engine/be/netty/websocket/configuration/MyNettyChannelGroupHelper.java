package com.cmcorg20230301.engine.be.netty.websocket.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.admin.websocket.model.constant.CommonConstant;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通道组，管理所有 WebSocket连接
 */
public class MyNettyChannelGroupHelper {

    public static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("userId");
    // socket连接记录的 主键id
    public static final AttributeKey<Long> WEB_SOCKET_ID_KEY = AttributeKey.valueOf(CommonConstant.WEB_SOCKET_ID);

    public static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 根据 key 查找 channel
     */
    public static List<Channel> getChannelByIdSet(AttributeKey<Long> key, Set<Number> idSet) {
        if (CollUtil.isEmpty(idSet)) {
            return new ArrayList<>(); // 防止空指针
        }
        Set<Long> idLongSet = idSet.stream().map(Convert::toLong).collect(Collectors.toSet());
        return CHANNEL_GROUP.stream().filter(channel -> idLongSet.contains(channel.attr(key).get()))
            .collect(Collectors.toList());
    }

    /**
     * 发送给所有人
     */
    public static void sendToAll(TextWebSocketFrame textWebSocketFrame) {
        CHANNEL_GROUP.writeAndFlush(textWebSocketFrame);
    }
}


