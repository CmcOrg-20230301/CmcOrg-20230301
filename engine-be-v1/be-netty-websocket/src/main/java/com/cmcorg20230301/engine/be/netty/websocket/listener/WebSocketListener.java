package com.cmcorg20230301.engine.be.netty.websocket.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.enums.WebSocketMessageEnum;
import com.admin.websocket.configuration.MyNettyChannelGroupHelper;
import com.admin.websocket.util.MyWebSocketUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * WebSocket 监听器
 */
@Component
@KafkaListener(topics = {BaseConstant.MQ_WEB_SOCKET_TOPIC}, containerFactory = "dynamicGroupIdContainerFactory")
public class WebSocketListener {

    @KafkaHandler
    public void receive(String jsonStr) {

        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

        Integer code = jsonObject.getInt("code");
        if (code == null) {
            return;
        }

        WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.getByCode(code);
        if (webSocketMessageEnum == null) {
            return;
        }

        BeanUtil.copyProperties(jsonObject, webSocketMessageEnum); // 属性拷贝

        handleWebSocketMessageEnum(webSocketMessageEnum);
    }

    /**
     * 处理 webSocketMessageEnum
     */
    private void handleWebSocketMessageEnum(WebSocketMessageEnum webSocketMessageEnum) {

        int code = webSocketMessageEnum.getCode();
        JSONObject json = webSocketMessageEnum.getJson();
        List<Channel> channelList;

        if (code >= 2 && code <= 4) {

            // 2 账号已在其他地方登录，您被迫下线
            // 3 登录过期，请重新登录
            // 4 账号已被注销
            Set<Number> idSet = json.get("userIdSet", Set.class);

            if (CollUtil.isEmpty(idSet)) {
                idSet = json.get("webSocketIdSet", Set.class);
                channelList =
                    MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.WEB_SOCKET_ID_KEY, idSet);
            } else {
                channelList = MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.USER_ID_KEY, idSet);
            }

            if (CollUtil.isNotEmpty(channelList)) {
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        } else if (code == 5 || (code >= 7 && code <= 9)) {

            // 5 有新的通知
            // 7 有新的好友申请
            // 8 好友申请已通过
            // 9 即时通讯，发送消息
            Set<Number> userIdSet = json.get("userIdSet", Set.class);

            channelList = MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.USER_ID_KEY, userIdSet);

            if (CollUtil.isNotEmpty(channelList)) {
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        } else if (code == 6) {

            // 6 有新的公告
            Set<Number> userIdSet = json.get("userIdSet", Set.class);

            if (CollUtil.isEmpty(userIdSet)) {
                writeAndFlush(null, webSocketMessageEnum);
            } else {
                channelList =
                    MyNettyChannelGroupHelper.getChannelByIdSet(MyNettyChannelGroupHelper.USER_ID_KEY, userIdSet);
                writeAndFlush(channelList, webSocketMessageEnum);
            }

        }
    }

    /**
     * 推送消息
     */
    private void writeAndFlush(List<Channel> channelList, WebSocketMessageEnum webSocketMessageEnum) {

        webSocketMessageEnum.setJson(null);

        TextWebSocketFrame textWebSocketFrame = MyWebSocketUtil.getTextWebSocketFrame(webSocketMessageEnum);

        if (channelList == null) {
            MyNettyChannelGroupHelper.sendToAll(textWebSocketFrame); // 发送给所有人
        } else {
            channelList.forEach(it -> it.writeAndFlush(textWebSocketFrame));
        }

    }

}
