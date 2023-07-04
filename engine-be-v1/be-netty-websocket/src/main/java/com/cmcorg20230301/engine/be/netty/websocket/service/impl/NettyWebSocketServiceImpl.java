package com.cmcorg20230301.engine.be.netty.websocket.service.impl;

import cn.hutool.core.util.IdUtil;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.netty.websocket.model.vo.NettyWebSocketRegisterVO;
import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketService;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import com.cmcorg20230301.engine.be.socket.service.SysSocketRefUserService;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class NettyWebSocketServiceImpl implements NettyWebSocketService {

    @Resource
    SysSocketService sysSocketService;

    @Resource
    SysSocketRefUserService sysSocketRefUserService;

    /**
     * 获取：webSocket连接地址和随机码
     */
    @Override
    public NettyWebSocketRegisterVO register(NotNullId notNullId) {

        // 找到连接数最少的那个：webSocket服务器
        SysSocketDO sysSocketDO = new SysSocketDO();

        sysSocketDO.setType(SysSocketTypeEnum.WEB_SOCKET);

        // 获取：最小连接数的 socket对象
        sysSocketDO = sysSocketService.getSocketDOOfMinConnectNumber(sysSocketDO);

        if (sysSocketDO == null) {
            return null;
        }

        NettyWebSocketRegisterVO nettyWebSocketRegisterVO = new NettyWebSocketRegisterVO();

        nettyWebSocketRegisterVO.setWebSocketUrl(sysSocketDO.getHost() + ":" + sysSocketDO.getPort());
        nettyWebSocketRegisterVO.setCode(IdUtil.simpleUUID());

        return nettyWebSocketRegisterVO;

    }

}
