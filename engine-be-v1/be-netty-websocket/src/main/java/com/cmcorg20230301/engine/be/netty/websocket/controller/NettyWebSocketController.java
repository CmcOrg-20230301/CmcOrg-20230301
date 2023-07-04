package com.cmcorg20230301.engine.be.netty.websocket.controller;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.netty.websocket.model.vo.NettyWebSocketRegisterVO;
import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketService;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/netty/webSocket")
@Tag(name = "netty-webSocket")
public class NettyWebSocketController {

    @Resource
    NettyWebSocketService baseService;

    @PostMapping("/register")
    @Operation(summary = "获取：webSocket连接地址和随机码")
    public ApiResultVO<NettyWebSocketRegisterVO> register(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.register(notNullId));
    }

}
