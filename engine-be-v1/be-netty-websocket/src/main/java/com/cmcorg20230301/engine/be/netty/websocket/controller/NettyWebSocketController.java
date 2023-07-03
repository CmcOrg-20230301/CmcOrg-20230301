package com.cmcorg20230301.engine.be.netty.websocket.controller;

import com.cmcorg20230301.engine.be.netty.websocket.service.NettyWebSocketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/netty/webSocket")
@Tag(name = "netty-webSocket")
public class NettyWebSocketController {

    @Resource
    NettyWebSocketService baseService;

    //    @PostMapping("/register")
    //    @Operation(summary = "获取：webSocket连接地址和随机码")
    //    public ApiResultVO<NettyWebSocketRegisterVO> register(@RequestBody @Valid NotNullId notNullId) {
    //        return ApiResultVO.ok(baseService.register(notNullId));
    //    }
    //
    //    @PreAuthorize("hasAuthority('nettyWebSocket:page')")
    //    @PostMapping("/page")
    //    @Operation(summary = "分页排序查询")
    //    public ApiResultVO<Page<SysSocketDO>> myPage(@RequestBody @Valid NettyWebSocketPageDTO dto) {
    //        return ApiResultVO.ok(baseService.myPage(dto));
    //    }
    //
    //    @PostMapping("/changeOnlineType")
    //    @Operation(summary = "更改在线状态")
    //    public ApiResultVO<String> changeOnlineType(@RequestBody @Valid ChangeNumberDTO dto) {
    //        return ApiResultVO.ok(baseService.changeOnlineType(dto));
    //    }
    //
    //    @PreAuthorize("hasAuthority('nettyWebSocket:insertOrUpdate')")
    //    @PostMapping("/retreatByIdSet")
    //    @Operation(summary = "强退，通过 idSet")
    //    public ApiResultVO<String> retreatByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
    //        return ApiResultVO.ok(baseService.retreatAndNoticeByIdSet(notEmptyIdSet));
    //    }
    //
    //    @PreAuthorize("hasAuthority('nettyWebSocket:insertOrUpdate')")
    //    @PostMapping("/retreatAll")
    //    @Operation(summary = "全部强退")
    //    public ApiResultVO<String> retreatAll() {
    //        return ApiResultVO.ok(baseService.retreatAndNoticeAll());
    //    }

}
