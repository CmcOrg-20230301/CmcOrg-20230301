package com.cmcorg20230301.engine.be.netty.websocket.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullInteger;
import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketService;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.engine.be.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketOnlineTypeEnum;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
import com.cmcorg20230301.engine.be.util.util.CallBack;
import com.cmcorg20230301.engine.be.util.util.MyMapUtil;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    public Set<String> getAllWebSocketUrl(NotNullInteger notNullInteger) {

        CallBack<Long> expireTsCallBack = new CallBack<>();

        // 获取：请求里面的 jwtHash值
        String jwtHash = MyJwtUtil.getJwtHashByRequest(httpServletRequest, null, expireTsCallBack);

        if (StrUtil.isBlank(jwtHash)) {
            return null;
        }

        // 获取：所有 webSocket
        List<SysSocketDO> sysSocketDOList =
            sysSocketService.lambdaQuery().eq(SysSocketDO::getType, SysSocketTypeEnum.WEB_SOCKET)
                .eq(BaseEntityNoId::getEnableFlag, true).list();

        if (CollUtil.isEmpty(sysSocketDOList)) {
            return new HashSet<>();
        }

        String currentUserNickName = UserUtil.getCurrentUserNickName();

        Long currentUserId = UserUtil.getCurrentUserId();

        SysSocketOnlineTypeEnum sysSocketOnlineTypeEnum = SysSocketOnlineTypeEnum.getByCode(notNullInteger.getValue());

        String ip = ServletUtil.getClientIP(httpServletRequest);

        String region = Ip2RegionUtil.getRegion(ip);

        SysRequestCategoryEnum sysRequestCategoryEnum = RequestUtil.getRequestCategoryEnum(httpServletRequest);

        String userAgentStr = httpServletRequest.getHeader(Header.USER_AGENT.getValue());

        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);

        String userAgentJsonStr = JSONUtil.toJsonStr(userAgent);

        HashSet<String> resSet = new HashSet<>(MyMapUtil.getInitialCapacity(sysSocketDOList.size()));

        for (SysSocketDO item : sysSocketDOList) {

            // 处理：获取：所有 webSocket连接地址
            handleGetAllWebSocketUrl(expireTsCallBack, jwtHash, currentUserNickName, currentUserId,
                sysSocketOnlineTypeEnum, ip, region, sysRequestCategoryEnum, userAgentJsonStr, resSet, item);

        }

        return resSet;

    }

    /**
     * 处理：获取：所有 webSocket连接地址
     */
    private void handleGetAllWebSocketUrl(CallBack<Long> expireTsCallBack, String jwtHash, String currentUserNickName,
        Long currentUserId, SysSocketOnlineTypeEnum sysSocketOnlineTypeEnum, String ip, String region,
        SysRequestCategoryEnum sysRequestCategoryEnum, String userAgentJsonStr, HashSet<String> resSet,
        SysSocketDO sysSocketDO) {

        String code = IdUtil.simpleUUID();

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(sysSocketDO.getScheme()).append(sysSocketDO.getHost());

        if ("ws://".equals(sysSocketDO.getScheme())) { // ws，才需要端口号

            strBuilder.append(":").append(sysSocketDO.getPort());

        }

        strBuilder.append(sysSocketDO.getPath()).append("?code=").append(code);

        resSet.add(strBuilder.toString()); // 添加到返回值里

        String key = RedisKeyEnum.PRE_WEB_SOCKET_CODE.name() + code;

        SysSocketRefUserDO sysSocketRefUserDO = new SysSocketRefUserDO();

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

        sysSocketRefUserDO.setCreateId(currentUserId);
        sysSocketRefUserDO.setUpdateId(currentUserId);
        sysSocketRefUserDO.setEnableFlag(true);
        sysSocketRefUserDO.setDelFlag(false);
        sysSocketRefUserDO.setRemark("");

        // 设置到：redis里面，用于连接的时候用
        redissonClient.<SysSocketRefUserDO>getBucket(key)
            .set(sysSocketRefUserDO, BaseConstant.SHORT_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);

    }

}
