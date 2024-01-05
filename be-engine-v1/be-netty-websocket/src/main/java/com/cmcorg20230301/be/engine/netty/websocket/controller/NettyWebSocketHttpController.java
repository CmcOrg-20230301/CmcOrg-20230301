package com.cmcorg20230301.be.engine.netty.websocket.controller;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndIntegerValue;
import com.cmcorg20230301.be.engine.netty.websocket.service.NettyWebSocketService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping(value = "/netty/webSocket")
@Tag(name = "基础-netty-webSocket")
public class NettyWebSocketHttpController {

    @Resource
    NettyWebSocketService baseService;

    @PostMapping(value = "/getAllWebSocketUrl")
    @Operation(summary = "获取：所有 webSocket连接地址，格式：scheme://ip:port/path?code=xxx")
    public ApiResultVO<Set<String>> getAllWebSocketUrl() {
        return ApiResultVO.okData(baseService.getAllWebSocketUrl());
    }

    @PostMapping(value = "/getWebSocketUrlById")
    @Operation(summary = "通过主键 id，获取：webSocket连接地址，格式：scheme://ip:port/path?code=xxx")
    public ApiResultVO<String> getWebSocketUrlById(
            @RequestBody @Valid NotNullIdAndIntegerValue notNullIdAndIntegerValue) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.getWebSocketUrlById(notNullIdAndIntegerValue));
    }

}
