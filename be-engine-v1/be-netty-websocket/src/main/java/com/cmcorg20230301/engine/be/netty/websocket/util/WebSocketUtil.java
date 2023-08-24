package com.cmcorg20230301.engine.be.netty.websocket.util;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.engine.be.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.engine.be.netty.websocket.configuration.NettyWebSocketBeanPostProcessor;
import com.cmcorg20230301.engine.be.netty.websocket.server.NettyWebSocketServerHandler;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.socket.model.dto.WebSocketMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class WebSocketUtil {

    // 目的：Long 转 String
    private static ObjectMapper objectMapper;

    @Resource
    public void setObjectMapper(ObjectMapper objectMapper) {
        WebSocketUtil.objectMapper = objectMapper;
    }

    /**
     * 发送消息
     */
    @SneakyThrows
    public static <T> void send(Channel channel, WebSocketMessageDTO<T> dto, String text, long costMs,
        @Nullable NettyWebSocketBeanPostProcessor.MappingValue mappingValue, String errorMsg) {

        Long userId = channel.attr(NettyWebSocketServerHandler.USER_ID_KEY).get();

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

        sysRequestDO.setSuccessFlag(StrUtil.isBlank(errorMsg));
        sysRequestDO.setErrorMsg(errorMsg);
        sysRequestDO.setRequestParam(text);
        sysRequestDO.setType(OperationDescriptionConstant.WEB_SOCKET);
        sysRequestDO.setResponseValue(jsonStr);

        sysRequestDO.setCreateId(userId);
        sysRequestDO.setCreateTime(date);
        sysRequestDO.setUpdateId(userId);
        sysRequestDO.setUpdateTime(date);

        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);
        sysRequestDO.setRemark("");

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

        channel.writeAndFlush(new TextWebSocketFrame(jsonStr));

    }

}
