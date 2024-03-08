package com.cmcorg20230301.be.engine.netty.websocket.service.impl;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.cmcorg20230301.be.engine.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndIntegerValue;
import com.cmcorg20230301.be.engine.netty.websocket.service.NettyWebSocketService;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.security.util.MyJwtUtil;
import com.cmcorg20230301.be.engine.security.util.RequestUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketOnlineTypeEnum;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.be.engine.socket.service.SysSocketService;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.cmcorg20230301.be.engine.util.util.MyMapUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;

@Service
public class NettyWebSocketServiceImpl implements NettyWebSocketService {

    @Resource
    SysSocketService sysSocketService;

    @Resource
    RedissonClient redissonClient;

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 获取：所有 webSocket连接地址，格式：scheme://ip:port/path?code=xxx
     */
    @Override
    public Set<String> getAllWebSocketUrl() {

        // 获取：webSocket连接地址
        return handleGetAllWebSocketUrl(null, SysSocketOnlineTypeEnum.PING_TEST);

    }

    /**
     * 获取：webSocket连接地址
     */
    @NotNull
    private HashSet<String> handleGetAllWebSocketUrl(@Nullable List<SysSocketDO> sysSocketDOList,
        @NotNull SysSocketOnlineTypeEnum sysSocketOnlineTypeEnum) {

        CallBack<Long> expireTsCallBack = new CallBack<>();

        // 获取：请求里面的 jwtHash值
        String jwtHash = MyJwtUtil.getJwtHashByRequest(httpServletRequest, null, expireTsCallBack);

        if (StrUtil.isBlank(jwtHash)) {
            return new HashSet<>();
        }

        // 获取：所有 webSocket
        if (sysSocketDOList == null) {

            sysSocketDOList = sysSocketService.lambdaQuery().eq(SysSocketDO::getType, SysSocketTypeEnum.WEB_SOCKET)
                .eq(BaseEntityNoId::getEnableFlag, true).list();

        }

        if (CollUtil.isEmpty(sysSocketDOList)) {
            return new HashSet<>();
        }

        String currentUserNickName = UserUtil.getCurrentUserNickName();

        Long currentUserId = UserUtil.getCurrentUserId();

        String ip = ServletUtil.getClientIP(httpServletRequest);

        String region = Ip2RegionUtil.getRegion(ip);

        SysRequestCategoryEnum sysRequestCategoryEnum = RequestUtil.getRequestCategoryEnum(httpServletRequest);

        String userAgentStr = httpServletRequest.getHeader(Header.USER_AGENT.getValue());

        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);

        String userAgentJsonStr = JSONUtil.toJsonStr(userAgent);

        HashSet<String> resSet = new HashSet<>(MyMapUtil.getInitialCapacity(sysSocketDOList.size()));

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        for (SysSocketDO item : sysSocketDOList) {

            // 处理：获取：所有 webSocket连接地址
            doHandleGetAllWebSocketUrl(expireTsCallBack, jwtHash, currentUserNickName, currentUserId, ip, region,
                sysRequestCategoryEnum, userAgentJsonStr, resSet, item, sysSocketOnlineTypeEnum,
                currentTenantIdDefault);

        }

        return resSet;

    }

    /**
     * 通过主键 id，获取：webSocket连接地址，格式：scheme://ip:port/path?code=xxx
     */
    @Override
    public String getWebSocketUrlById(NotNullIdAndIntegerValue notNullIdAndIntegerValue) {

        SysSocketDO sysSocketDO = sysSocketService.lambdaQuery().eq(BaseEntity::getId, notNullIdAndIntegerValue.getId())
            .eq(SysSocketDO::getType, SysSocketTypeEnum.WEB_SOCKET).eq(BaseEntityNoId::getEnableFlag, true).one();

        Integer value = notNullIdAndIntegerValue.getValue();

        SysSocketOnlineTypeEnum sysSocketOnlineTypeEnum = SysSocketOnlineTypeEnum.getByCode(value);

        // 获取：webSocket连接地址
        Set<String> webSocketUrlSet =
            handleGetAllWebSocketUrl(CollUtil.newArrayList(sysSocketDO), sysSocketOnlineTypeEnum);

        return CollUtil.getFirst(webSocketUrlSet);

    }

    /**
     * 处理：获取：所有 webSocket连接地址
     */
    private void doHandleGetAllWebSocketUrl(CallBack<Long> expireTsCallBack, String jwtHash, String currentUserNickName,
        Long currentUserId, String ip, String region, SysRequestCategoryEnum sysRequestCategoryEnum,
        String userAgentJsonStr, HashSet<String> resSet, SysSocketDO sysSocketDO,
        SysSocketOnlineTypeEnum sysSocketOnlineTypeEnum, Long currentTenantIdDefault) {

        String code = IdUtil.simpleUUID();

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(sysSocketDO.getScheme()).append(sysSocketDO.getHost());

        if ("ws://".equals(sysSocketDO.getScheme())) { // ws，才需要端口号

            strBuilder.append(":").append(sysSocketDO.getPort());

        }

        strBuilder.append(sysSocketDO.getPath()).append("?code=").append(code);

        resSet.add(strBuilder.toString()); // 添加到返回值里

        String key = BaseRedisKeyEnum.PRE_WEB_SOCKET_CODE.name() + code;

        SysSocketRefUserDO sysSocketRefUserDO = new SysSocketRefUserDO();

        Long nextId = IdGeneratorUtil.nextId();
        sysSocketRefUserDO.setId(nextId); // 备注：这里手动设置 id

        sysSocketRefUserDO.setUserId(currentUserId);
        sysSocketRefUserDO.setSocketId(sysSocketDO.getId());
        sysSocketRefUserDO.setNickname(currentUserNickName);
        sysSocketRefUserDO.setScheme(sysSocketDO.getScheme());
        sysSocketRefUserDO.setHost(sysSocketDO.getHost());
        sysSocketRefUserDO.setPort(sysSocketDO.getPort());
        sysSocketRefUserDO.setPath(sysSocketDO.getPath());
        sysSocketRefUserDO.setType(sysSocketDO.getType());

        sysSocketRefUserDO.setOnlineType(sysSocketOnlineTypeEnum);
        sysSocketRefUserDO.setIp(ip);
        sysSocketRefUserDO.setRegion(region);

        sysSocketRefUserDO.setJwtHash(jwtHash);
        sysSocketRefUserDO.setJwtHashExpireTs(expireTsCallBack.getValue());

        sysSocketRefUserDO.setCategory(sysRequestCategoryEnum);

        sysSocketRefUserDO.setUserAgentJsonStr(userAgentJsonStr);

        sysSocketRefUserDO.setTenantId(currentTenantIdDefault);

        sysSocketRefUserDO.setCreateId(currentUserId);
        sysSocketRefUserDO.setUpdateId(currentUserId);

        sysSocketRefUserDO.setEnableFlag(sysSocketOnlineTypeEnum.equals(SysSocketOnlineTypeEnum.PING_TEST) == false);

        sysSocketRefUserDO.setDelFlag(false);
        sysSocketRefUserDO.setRemark("");

        // 设置到：redis里面，用于连接的时候用
        redissonClient.<SysSocketRefUserDO>getBucket(key).set(sysSocketRefUserDO,
            Duration.ofMillis(BaseConstant.SHORT_CODE_EXPIRE_TIME));

    }

}
