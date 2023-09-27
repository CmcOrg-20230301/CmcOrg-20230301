package com.cmcorg20230301.be.engine.netty.websocket.server;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.constant.OperationDescriptionConstant;
import com.cmcorg20230301.be.engine.netty.websocket.configuration.NettyWebSocketBeanPostProcessor;
import com.cmcorg20230301.be.engine.netty.websocket.properties.NettyWebSocketProperties;
import com.cmcorg20230301.be.engine.netty.websocket.util.WebSocketUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseException;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.RequestUtil;
import com.cmcorg20230301.be.engine.socket.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.be.engine.socket.service.SysSocketRefUserService;
import com.cmcorg20230301.be.engine.socket.util.SocketUtil;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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

    // UserId key
    public static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("USER_ID_KEY");

    // SysSocketRefUserId key
    public static final AttributeKey<Long> SYS_SOCKET_REF_USER_ID_KEY =
        AttributeKey.valueOf("SYS_SOCKET_REF_USER_ID_KEY");

    // SysRequestCategoryEnum key
    public static final AttributeKey<SysRequestCategoryEnum> SYS_REQUEST_CATEGORY_ENUM_KEY =
        AttributeKey.valueOf("SYS_REQUEST_CATEGORY_ENUM_KEY");

    // Ip key
    public static final AttributeKey<String> IP_KEY = AttributeKey.valueOf("IP_KEY");

    // TenantId key
    public static final AttributeKey<Long> TENANT_ID_KEY = AttributeKey.valueOf("TENANT_ID_KEY");

    // 用户通道 map，大key：用户主键 id，小key：sysSocketRefUserId，value：通道
    public static final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Channel>> USER_ID_CHANNEL_MAP =
        MapUtil.newConcurrentHashMap();

    private CopyOnWriteArraySet<Long> SYS_SOCKET_REF_USER_ID_SET = new CopyOnWriteArraySet<>();

    private CopyOnWriteArrayList<SysSocketRefUserDO> SYS_SOCKET_REF_USER_DO_LIST = new CopyOnWriteArrayList<>();

    /**
     * 定时任务，保存数据
     */
    @PreDestroy
    @Scheduled(fixedDelay = 5000)
    public void scheduledSava() {

        // 处理：SYS_SOCKET_REF_USER_DO_LIST
        handleSysSocketRefUserDOList();

        // 处理：SYS_SOCKET_REF_USER_ID_SET
        handleSysSocketRefUserIdSet();

    }

    /**
     * 处理：SYS_SOCKET_REF_USER_DO_LIST
     */
    private void handleSysSocketRefUserDOList() {

        CopyOnWriteArrayList<SysSocketRefUserDO> tempSysSocketRefUserDOList;

        synchronized (SYS_SOCKET_REF_USER_DO_LIST) {

            if (CollUtil.isEmpty(SYS_SOCKET_REF_USER_DO_LIST)) {
                return;
            }

            tempSysSocketRefUserDOList = SYS_SOCKET_REF_USER_DO_LIST;
            SYS_SOCKET_REF_USER_DO_LIST = new CopyOnWriteArrayList<>();

        }

        int sum = USER_ID_CHANNEL_MAP.values().stream().mapToInt(it -> it.values().size()).sum();

        log.info("WebSocket 保存数据，长度：{}，连接总数：{}", tempSysSocketRefUserDOList.size(), sum);

        sysSocketRefUserService.saveBatch(tempSysSocketRefUserDOList);

    }

    /**
     * 处理：SYS_SOCKET_REF_USER_ID_SET
     */
    private void handleSysSocketRefUserIdSet() {

        CopyOnWriteArraySet<Long> tempSysSocketRefUserIdSet;

        synchronized (SYS_SOCKET_REF_USER_ID_SET) {

            if (CollUtil.isEmpty(SYS_SOCKET_REF_USER_ID_SET)) {
                return;
            }

            tempSysSocketRefUserIdSet = SYS_SOCKET_REF_USER_ID_SET;
            SYS_SOCKET_REF_USER_ID_SET = new CopyOnWriteArraySet<>();

        }

        int sum = USER_ID_CHANNEL_MAP.values().stream().mapToInt(it -> it.values().size()).sum();

        log.info("WebSocket 移除数据，长度：{}，连接总数：{}", tempSysSocketRefUserIdSet.size(), sum);

        sysSocketRefUserService.removeByIds(tempSysSocketRefUserIdSet);

    }

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

        if (userId != null) {

            Long sysSocketRefUserId = channel.attr(SYS_SOCKET_REF_USER_ID_KEY).get();

            ConcurrentHashMap<Long, Channel> channelMap =
                USER_ID_CHANNEL_MAP.computeIfAbsent(userId, k -> MapUtil.newConcurrentHashMap());

            channelMap.remove(sysSocketRefUserId);

            log.info("WebSocket 断开，用户：{}，连接数：{}", userId, channelMap.size());

            SYS_SOCKET_REF_USER_ID_SET.add(sysSocketRefUserId);

        }

        super.channelInactive(ctx);

    }

    /**
     * 发生异常时，比如：远程主机强迫关闭了一个现有的连接，或者任何没有被捕获的异常
     */
    @SneakyThrows
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {

        ctx.close(); // 会执行：channelInactive 方法

        super.exceptionCaught(ctx, e); // 会打印日志

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

        long costMs = System.currentTimeMillis();

        String text = textWebSocketFrame.text();

        WebSocketMessageDTO<?> dto = JSONUtil.toBean(text, WebSocketMessageDTO.class);

        String uri = dto.getUri();

        NettyWebSocketBeanPostProcessor.MappingValue mappingValue =
            NettyWebSocketBeanPostProcessor.getMappingValueByKey(uri);

        if (mappingValue == null) {

            WebSocketMessageDTO<?> webSocketMessageDTO = new WebSocketMessageDTO<>();

            webSocketMessageDTO.setUri(uri);
            webSocketMessageDTO.setCode(404);

            WebSocketUtil.send(channel, webSocketMessageDTO, text, costMs, null, "", false);

            return;

        }

        Parameter[] parameterArr = mappingValue.getMethod().getParameters();

        Object[] args = null;

        if (ArrayUtil.isNotEmpty(parameterArr)) {

            Parameter parameter = parameterArr[0];

            Object object = BeanUtil.toBean(dto.getData(), parameter.getType());

            args = new Object[] {object};

        }

        try {

            Object invoke = ReflectUtil.invokeRaw(mappingValue.getBean(), mappingValue.getMethod(), args);

            WebSocketMessageDTO<Object> webSocketMessageDTO = new WebSocketMessageDTO<>();

            webSocketMessageDTO.setUri(uri);

            if (invoke instanceof ApiResultVO) {

                ApiResultVO<?> apiResultVO = (ApiResultVO<?>)invoke;

                webSocketMessageDTO.setCode(apiResultVO.getCode());
                webSocketMessageDTO.setData(apiResultVO.getData());

            } else {

                webSocketMessageDTO.setCode(200);
                webSocketMessageDTO.setData(invoke);

            }

            WebSocketUtil.send(channel, webSocketMessageDTO, text, costMs, mappingValue, "", true);

        } catch (Throwable e) {

            if (e instanceof InvocationTargetException) {

                e = ((InvocationTargetException)e).getTargetException();

            }

            e.printStackTrace();

            WebSocketMessageDTO<Object> webSocketMessageDTO = new WebSocketMessageDTO<>();

            if (e instanceof BaseException) {

                ApiResultVO<?> apiResultVO = ((BaseException)e).getApiResultVO();

                webSocketMessageDTO.setUri(uri);
                webSocketMessageDTO.setCode(apiResultVO.getCode());
                webSocketMessageDTO.setMsg(apiResultVO.getMsg());

            } else {

                webSocketMessageDTO.setUri(uri);
                webSocketMessageDTO.setCode(BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode());
                webSocketMessageDTO.setMsg(BaseBizCodeEnum.API_RESULT_SYS_ERROR.getMsg());

            }

            WebSocketUtil.send(channel, webSocketMessageDTO, text, costMs, mappingValue,
                MyEntityUtil.getNotNullStr(e.getMessage()), false);

        }

    }

    /**
     * 处理：FullHttpRequest
     */
    private void handleFullHttpRequest(@NotNull ChannelHandlerContext ctx, @NotNull FullHttpRequest fullHttpRequest) {

        UrlQuery urlQuery = UrlQuery.of(fullHttpRequest.uri(), CharsetUtil.CHARSET_UTF_8);

        String code = Convert.toStr(urlQuery.get("code")); // 随机码

        if (StrUtil.isBlank(code)) {

            handleFullHttpRequestError(ctx, fullHttpRequest.uri(), "code为空", fullHttpRequest);

            return;

        }

        String key = BaseRedisKeyEnum.PRE_WEB_SOCKET_CODE.name() + code;

        SysSocketRefUserDO sysSocketRefUserDO = redissonClient.<SysSocketRefUserDO>getBucket(key).getAndDelete();

        if (sysSocketRefUserDO == null) {

            handleFullHttpRequestError(ctx, fullHttpRequest.uri(), "SysSocketRefUserDO为null",
                fullHttpRequest); // 处理：非法连接

            return;

        }

        if (!sysSocketRefUserDO.getSocketId().equals(NettyWebSocketServer.sysSocketServerId)) {

            handleFullHttpRequestError(ctx, fullHttpRequest.uri(), "SocketId不相同", fullHttpRequest); // 处理：非法连接

            return;

        }

        // url包含参数，需要舍弃
        fullHttpRequest.setUri(nettyWebSocketProperties.getPath());

        // 处理：上线操作
        onlineHandle(ctx.channel(), sysSocketRefUserDO, fullHttpRequest);

    }

    /**
     * 处理：上线操作
     */
    private void onlineHandle(Channel channel, SysSocketRefUserDO sysSocketRefUserDO,
        @NotNull FullHttpRequest fullHttpRequest) {

        SYS_SOCKET_REF_USER_DO_LIST.add(sysSocketRefUserDO);

        Long userId = sysSocketRefUserDO.getUserId();

        Long sysSocketRefUserDoId = sysSocketRefUserDO.getId();

        Long tenantId = sysSocketRefUserDO.getTenantId();

        // 绑定 UserId
        channel.attr(USER_ID_KEY).set(userId);

        // 绑定 SysSocketRefUserId
        channel.attr(SYS_SOCKET_REF_USER_ID_KEY).set(sysSocketRefUserDoId);

        // 绑定 SysRequestCategoryEnum
        channel.attr(SYS_REQUEST_CATEGORY_ENUM_KEY).set(sysSocketRefUserDO.getCategory());

        // 绑定 Ip
        channel.attr(IP_KEY).set(SocketUtil.getIp(fullHttpRequest, channel));

        // 绑定 TenantId
        channel.attr(TENANT_ID_KEY).set(tenantId);

        ConcurrentHashMap<Long, Channel> channelMap =
            USER_ID_CHANNEL_MAP.computeIfAbsent(userId, k -> MapUtil.newConcurrentHashMap());

        channelMap.put(sysSocketRefUserDoId, channel);

        log.info("WebSocket 连接，用户：{}，连接数：{}", userId, channelMap.size());

    }

    /**
     * 处理：非法连接
     */
    private void handleFullHttpRequestError(@NotNull ChannelHandlerContext ctx, String requestParam, String errorMsg,
        @NotNull FullHttpRequest fullHttpRequest) {

        ctx.close(); // 关闭连接

        Date date = new Date();

        SysRequestDO sysRequestDO = new SysRequestDO();

        sysRequestDO.setUri("");
        sysRequestDO.setCostMsStr("");
        sysRequestDO.setCostMs(0L);
        sysRequestDO.setName("WebSocket连接错误");
        sysRequestDO.setCategory(SysRequestCategoryEnum.PC_BROWSER_WINDOWS);

        sysRequestDO.setIp(SocketUtil.getIp(fullHttpRequest, ctx.channel()));
        sysRequestDO.setRegion(Ip2RegionUtil.getRegion(sysRequestDO.getIp()));

        sysRequestDO.setSuccessFlag(false);
        sysRequestDO.setErrorMsg(errorMsg);
        sysRequestDO.setRequestParam(requestParam);
        sysRequestDO.setType(OperationDescriptionConstant.WEB_SOCKET_CONNECT_ERROR);
        sysRequestDO.setResponseValue("");

        sysRequestDO.setCreateTime(date);
        sysRequestDO.setUpdateTime(date);

        sysRequestDO.setEnableFlag(true);
        sysRequestDO.setDelFlag(false);
        sysRequestDO.setRemark("");

        // 添加一个：请求数据
        RequestUtil.add(sysRequestDO);

    }

}
