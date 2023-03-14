package com.cmcorg20230301.engine.be.netty.websocket.controller;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.websocket.model.dto.SysWebSocketPageDTO;
import com.admin.websocket.model.entity.SysWebSocketDO;
import com.admin.websocket.model.vo.SysWebSocketRegisterVO;
import com.admin.websocket.service.SysWebSocketService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/sysWebSocket")
@Api(tags = "webSocket")
public class SysWebSocketController {

    @Resource
    SysWebSocketService baseService;

    @PostMapping("/register")
    @ApiOperation(value = "获取 webSocket连接地址和随机码")
    public ApiResultVO<SysWebSocketRegisterVO> register(@RequestBody @Valid NotNullByte notNullByte) {
        return ApiResultVO.ok(baseService.register(notNullByte));
    }

    @PreAuthorize("hasAuthority('sysWebSocket:page')")
    @PostMapping("/page")
    @ApiOperation(value = "分页排序查询")
    public ApiResultVO<Page<SysWebSocketDO>> myPage(@RequestBody @Valid SysWebSocketPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @PostMapping("/changeType")
    @ApiOperation(value = "更改在线状态")
    public ApiResultVO<String> changeType(@RequestBody @Valid NotNullByteAndId notNullByteAndId) {
        return ApiResultVO.ok(baseService.changeType(notNullByteAndId));
    }

    @PreAuthorize("hasAuthority('sysWebSocket:insertOrUpdate')")
    @PostMapping("/retreatByIdSet")
    @ApiOperation(value = "强退，通过 idSet")
    public ApiResultVO<String> retreatByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.retreatAndNoticeByIdSet(notEmptyIdSet));
    }

    @PreAuthorize("hasAuthority('sysWebSocket:insertOrUpdate')")
    @PostMapping("/retreatAll")
    @ApiOperation(value = "全部强退")
    public ApiResultVO<String> retreatAll() {
        return ApiResultVO.ok(baseService.retreatAndNoticeAll());
    }

}
