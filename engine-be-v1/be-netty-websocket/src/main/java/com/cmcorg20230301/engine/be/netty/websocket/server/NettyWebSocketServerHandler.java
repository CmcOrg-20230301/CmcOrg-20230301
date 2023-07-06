package com.cmcorg20230301.engine.be.netty.websocket.server;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.engine.be.netty.websocket.configuration.NettyWebSocketBeanPostProcessor;
import com.cmcorg20230301.engine.be.netty.websocket.properties.NettyWebSocketProperties;
import com.cmcorg20230301.engine.be.netty.websocket.util.WebSocketUtil;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;
import com.cmcorg20230301.engine.be.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.socket.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.engine.be.socket.service.SysSocketRefUserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = LogTopicConstant.NETTY_WEB_SOCKET)
public class NettyWebSocketServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    NettyWebSocketProperties nettyWebSocketProperties;

    @Resource
    RedissonClient redissonClient;

    @Resource
    SysSocketRefUserService sysSocketRefUserService;

    // userId key
    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("USER_ID_KEY");

    // sysSocketRefUserId key
    private static final AttributeKey<Long> SYS_SOCKET_REF_USER_ID_KEY =
        AttributeKey.valueOf("SYS_SOCKET_REF_USER_ID_KEY");

    // 用户通道 map，大key：用户主键 id，小key：sysSocketRefUserId，value：通道
    private static final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Channel>> USER_ID_CHANNEL_MAP =
        MapUtil.newConcurrentHashMap();

    /**
     * 连接成功时
     */
    @SneakyThrows
    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {

        super.channelActive(ctx);

    }

    /**
     * 调用 close等操作，连接断开时
     */
    @SneakyThrows
    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {

        Channel channel = ctx.channel();

        Long userId = channel.attr(USER_ID_KEY).get();

        Long sysSocketRefUserId = channel.attr(SYS_SOCKET_REF_USER_ID_KEY).get();

        ConcurrentHashMap<Long, Channel> channelMap =
            USER_ID_CHANNEL_MAP.computeIfAbsent(userId, k -> MapUtil.newConcurrentHashMap());

        channelMap.remove(sysSocketRefUserId);

        log.info("WebSocket 连接断开，用户：{}，连接数：{}", userId, channelMap.size());

        sysSocketRefUserService.removeById(sysSocketRefUserId);

        super.channelInactive(ctx);

    }

    /**
     * 发生异常时，比如：远程主机强迫关闭了一个现有的连接
     */
    @SneakyThrows
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

        ctx.close(); // 会执行：channelInactive 方法

        super.exceptionCaught(ctx, e);

    }

    /**
     * 收到消息时
     */
    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {

        // 首次连接是 FullHttpRequest，处理参数
        if (msg instanceof FullHttpRequest) {

            try {

                // 处理：FullHttpRequest
                handleFullHttpRequest(ctx, (FullHttpRequest)msg);

                // 传递给下一个 handler，备注：这里不需要释放资源
                ctx.fireChannelRead(msg);

            } catch (Exception e) {

                ReferenceCountUtil.release(msg); // 备注：这里需要释放资源

                throw e;

            }

        } else if (msg instanceof TextWebSocketFrame) {

            try {

                // 处理：TextWebSocketFrame
                handleTextWebSocketFrame((TextWebSocketFrame)msg, ctx.channel());

            } finally {

                ReferenceCountUtil.release(msg); // 备注：这里需要释放资源

            }

        } else {

            // 传递给下一个 handler，备注：这里不需要释放资源
            ctx.fireChannelRead(msg);

        }

    }

    /**
     * 处理：TextWebSocketFrame
     */
    private void handleTextWebSocketFrame(@NotNull TextWebSocketFrame textWebSocketFrame, Channel channel) {

        String text = textWebSocketFrame.text();

        WebSocketMessageDTO<?> dto = JSONUtil.toBean(text, WebSocketMessageDTO.class);

        String uri = dto.getUri();

        NettyWebSocketBeanPostProcessor.MappingValue mappingValue =
            NettyWebSocketBeanPostProcessor.getMappingValueByKey(uri);

        if (mappingValue == null) {

            WebSocketMessageDTO<?> webSocketMessageDTO = new WebSocketMessageDTO<>();

            webSocketMessageDTO.setUri(uri);
            webSocketMessageDTO.setCode(404);

            WebSocketUtil.send(channel, webSocketMessageDTO);

            return;

        }

        Parameter[] parameterArr = mappingValue.getMethod().getParameters();

        Object[] args = null;

        if (ArrayUtil.isNotEmpty(parameterArr)) {

            Parameter parameter = parameterArr[0];

            Class<?> type = parameter.getType();

            Object object = BeanUtil.toBean(dto.getData(), type);

            args = new Object[] {object};

        }

        try {

            Object invoke = ReflectUtil.invokeRaw(mappingValue.getBean(), mappingValue.getMethod(), args);

            WebSocketMessageDTO<Object> webSocketMessageDTO = new WebSocketMessageDTO<>();

            webSocketMessageDTO.setUri(uri);
            webSocketMessageDTO.setCode(200);
            webSocketMessageDTO.setData(invoke);

            WebSocketUtil.send(channel, webSocketMessageDTO);

        } catch (Throwable e) {

            if (e instanceof InvocationTargetException) {

                Throwable targetException = ((InvocationTargetException)e).getTargetException();

                targetException.printStackTrace();

            } else {

                e.printStackTrace();

            }

        }

    }

    /**
     * 处理：FullHttpRequest
     */
    private void handleFullHttpRequest(@NotNull ChannelHandlerContext ctx, @NotNull FullHttpRequest fullHttpRequest) {

        UrlQuery urlQuery = UrlQuery.of(fullHttpRequest.uri(), CharsetUtil.CHARSET_UTF_8);

        String code = Convert.toStr(urlQuery.get("code")); // 随机码

        if (StrUtil.isBlank(code)) {
            handleFullHttpRequestError(ctx, urlQuery.toString(), "code为空");
        }

        String key = RedisKeyEnum.PRE_WEB_SOCKET_CODE.name() + code;

        SysSocketRefUserDO sysSocketRefUserDO = redissonClient.<SysSocketRefUserDO>getBucket(key).getAndDelete();

        if (sysSocketRefUserDO == null) {
            handleFullHttpRequestError(ctx, urlQuery.toString(), "SysSocketRefUserDO为null"); // 处理：非法连接
        }

        if (!sysSocketRefUserDO.getSocketId().equals(NettyWebSocketServer.sysSocketServerId)) {
            handleFullHttpRequestError(ctx, urlQuery.toString(), "SocketId不相同"); // 处理：非法连接
        }

        // url包含参数，需要舍弃
        fullHttpRequest.setUri(nettyWebSocketProperties.getPath());

        // 处理：上线操作
        onlineHandle(ctx.channel(), sysSocketRefUserDO);

    }

    /**
     * 处理：上线操作
     */
    private void onlineHandle(Channel channel, SysSocketRefUserDO sysSocketRefUserDO) {

        sysSocketRefUserService.save(sysSocketRefUserDO);

        Long userId = sysSocketRefUserDO.getUserId();

        Long sysSocketRefUserDOId = sysSocketRefUserDO.getId();

        // 绑定 userId
        channel.attr(USER_ID_KEY).set(userId);

        // 绑定 sysSocketRefUserId
        channel.attr(SYS_SOCKET_REF_USER_ID_KEY).set(sysSocketRefUserDOId);

        ConcurrentHashMap<Long, Channel> channelMap =
            USER_ID_CHANNEL_MAP.computeIfAbsent(userId, k -> MapUtil.newConcurrentHashMap());

        channelMap.put(sysSocketRefUserDOId, channel);

        log.info("WebSocket 连接成功，用户：{}，连接数：{}", userId, channelMap.size());

    }

    /**
     * 处理：非法连接
     */
    private void handleFullHttpRequestError(@NotNull ChannelHandlerContext ctx, String requestParam, String errorMsg) {

        ctx.close();

        SysRequestDO sysRequestDO = new SysRequestDO();

        sysRequestDO.setUri("");
        sysRequestDO.setCostMsStr("");
        sysRequestDO.setCostMs(0L);
        sysRequestDO.setName("WebSocket连接错误");
        sysRequestDO.setCategory(SysRequestCategoryEnum.PC_BROWSER_WINDOWS);

        InetSocketAddress inetSocketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String ip = inetSocketAddress.getAddress().getHostAddress();

        sysRequestDO.setIp(ip);
        sysRequestDO.setRegion(Ip2RegionUtil.getRegion(sysRequestDO.getIp()));

        sysRequestDO.setSuccessFlag(false);
        sysRequestDO.setErrorMsg(errorMsg);
        sysRequestDO.setRequestParam(requestParam);
        sysRequestDO.setType(OperationDescriptionConstant.WEB_SOCKET_CONNECT_ERROR);
        sysRequestDO.setResponseValue("");
        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);
        sysRequestDO.setRemark("");

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

        ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

    }

}
