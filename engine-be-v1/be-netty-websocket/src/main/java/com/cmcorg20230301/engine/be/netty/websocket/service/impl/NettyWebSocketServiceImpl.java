package com.cmcorg20230301.engine.be.netty.websocket.service.impl;

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
import com.cmcorg20230301.engine.be.netty.websocket.model.vo.NettyWebSocketRegisterVO;
import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketService;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketOnlineTypeEnum;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.engine.be.socket.service.SysSocketRefUserService;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
import com.cmcorg20230301.engine.be.util.util.CallBack;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class NettyWebSocketServiceImpl implements NettyWebSocketService {

    @Resource
    SysSocketService sysSocketService;

    @Resource
    SysSocketRefUserService sysSocketRefUserService;

    @Resource
    RedissonClient redissonClient;

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 获取：webSocket连接地址
     */
    @Override
    public NettyWebSocketRegisterVO register(NotNullInteger notNullInteger) {

        CallBack<Long> jwtHashRemainMsCallBack = new CallBack<>();

        String jwtHash = MyJwtUtil.getJwtHashByRequest(httpServletRequest, jwtHashRemainMsCallBack);

        if (StrUtil.isBlank(jwtHash)) {
            return null;
        }

        SysSocketDO sysSocketDO = new SysSocketDO();

        sysSocketDO.setType(SysSocketTypeEnum.WEB_SOCKET);

        // 获取：最小连接数的 socket对象
        sysSocketDO = sysSocketService.getSocketDOOfMinConnectNumber(sysSocketDO);

        if (sysSocketDO == null) {
            return null;
        }

        String currentUserNickName = UserUtil.getCurrentUserNickName();

        NettyWebSocketRegisterVO nettyWebSocketRegisterVO = new NettyWebSocketRegisterVO();

        String code = IdUtil.simpleUUID();

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(sysSocketDO.getScheme()).append(sysSocketDO.getHost());

        if ("ws://".equals(sysSocketDO.getScheme())) { // ws，才需要端口号

            strBuilder.append(":").append(sysSocketDO.getPort());

        }

        strBuilder.append(sysSocketDO.getPath()).append("?code=").append(code);

        nettyWebSocketRegisterVO.setWebSocketUrl(strBuilder.toString());

        String key = RedisKeyEnum.PRE_WEB_SOCKET_CODE.name() + code;

        Long currentUserId = UserUtil.getCurrentUserId();

        SysSocketRefUserDO sysSocketRefUserDO = new SysSocketRefUserDO();

        sysSocketRefUserDO.setUserId(currentUserId);
        sysSocketRefUserDO.setSocketId(sysSocketDO.getId());
        sysSocketRefUserDO.setNickname(currentUserNickName);
        sysSocketRefUserDO.setScheme(sysSocketDO.getScheme());
        sysSocketRefUserDO.setHost(sysSocketDO.getHost());
        sysSocketRefUserDO.setPort(sysSocketDO.getPort());
        sysSocketRefUserDO.setPath(sysSocketDO.getPath());
        sysSocketRefUserDO.setType(sysSocketDO.getType());

        sysSocketRefUserDO.setOnlineType(SysSocketOnlineTypeEnum.getByCode(notNullInteger.getValue()));
        sysSocketRefUserDO.setIp(ServletUtil.getClientIP(httpServletRequest));
        sysSocketRefUserDO.setRegion(Ip2RegionUtil.getRegion(sysSocketRefUserDO.getIp()));

        sysSocketRefUserDO.setJwtHash(jwtHash);
        sysSocketRefUserDO.setJwtHashRemainMs(jwtHashRemainMsCallBack.getValue());

        sysSocketRefUserDO.setCategory(RequestUtil.getRequestCategoryEnum(httpServletRequest));

        String uaStr = httpServletRequest.getHeader(Header.USER_AGENT.getValue());

        UserAgent userAgent = UserAgentUtil.parse(uaStr);

        sysSocketRefUserDO.setUserAgentJsonStr(JSONUtil.toJsonStr(userAgent));

        sysSocketRefUserDO.setCreateId(currentUserId);
        sysSocketRefUserDO.setUpdateId(currentUserId);
        sysSocketRefUserDO.setEnableFlag(true);
        sysSocketRefUserDO.setDelFlag(false);
        sysSocketRefUserDO.setRemark("");

        // 设置到：redis里面，用于连接的时候用
        redissonClient.<SysSocketRefUserDO>getBucket(key)
            .set(sysSocketRefUserDO, BaseConstant.SHORT_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return nettyWebSocketRegisterVO;

    }

}
