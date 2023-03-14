package com.cmcorg20230301.engine.be.netty.websocket.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.enums.WebSocketMessageEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.websocket.model.constant.CommonConstant;
import com.admin.websocket.model.entity.SysWebSocketDO;
import com.admin.websocket.service.SysWebSocketService;
import com.admin.websocket.util.MyWebSocketUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype") // 多例
public class MyNettyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    JsonRedisTemplate<SysWebSocketDO> jsonRedisTemplate;
    @Resource
    SysWebSocketService sysWebSocketService;

    @SneakyThrows
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        Long socketId = ctx.channel().attr(MyNettyChannelGroupHelper.WEB_SOCKET_ID_KEY).get();

        sysWebSocketService.offlineByWebSocketIdSet(CollUtil.newHashSet(socketId)); // 调用离线方法

        super.channelInactive(ctx);
    }

    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 首次连接是 FullHttpRequest，处理参数
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest)msg;
            UrlQuery urlQuery = UrlQuery.of(fullHttpRequest.uri(), CharsetUtil.CHARSET_UTF_8);

            String code = Convert.toStr(urlQuery.get("code")); // 随机码

            if (StrUtil.isBlank(code)) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            }

            ValueOperations<String, SysWebSocketDO> ops = jsonRedisTemplate.opsForValue();

            String redisKey = NettyServer.webSocketRegisterCodePreKey + code;

            /**
             * {@link com.admin.websocket.service.impl.SysWebSocketServiceImpl#setWebSocketForRegister}
             */
            SysWebSocketDO sysWebSocketDO = ops.get(redisKey);
            if (sysWebSocketDO == null) {
                ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            }

            // 删除 redis中该 key，目的：只能用一次
            jsonRedisTemplate.delete(redisKey);

            sysWebSocketService.save(sysWebSocketDO); // 保存到数据库

            // 上线操作
            online(sysWebSocketDO, ctx.channel());

            // url包含参数，需要处理
            fullHttpRequest.setUri(NettyServer.WS);
        }

        super.channelRead(ctx, msg);
    }

    private void online(SysWebSocketDO sysWebSocketDO, Channel channel) {

        // 绑定 userId
        channel.attr(MyNettyChannelGroupHelper.USER_ID_KEY).set(sysWebSocketDO.getCreateId());
        // 绑定 WebSocket 连接记录 主键id
        channel.attr(MyNettyChannelGroupHelper.WEB_SOCKET_ID_KEY).set(sysWebSocketDO.getId());

        boolean add = MyNettyChannelGroupHelper.CHANNEL_GROUP.add(channel);// 备注：断开连接之后，ChannelGroup 会自动移除该通道

        if (add) {
            ThreadUtil.execute(() -> {
                WebSocketMessageEnum webSocketMessageEnum = WebSocketMessageEnum.SOCKET_ID;
                webSocketMessageEnum
                    .setJson(JSONUtil.createObj().set(CommonConstant.WEB_SOCKET_ID, sysWebSocketDO.getId()));
                channel.writeAndFlush(MyWebSocketUtil.getTextWebSocketFrame(webSocketMessageEnum)); // 给前端发送 socketId
            });
        }
    }

    /**
     * 收到消息时
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {

    }

}
