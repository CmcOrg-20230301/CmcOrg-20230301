package com.cmcorg20230301.engine.be.server.controller;

import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.server.model.vo.ServerWorkInfoVO;
import com.cmcorg20230301.engine.be.server.service.ServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/server")
@Tag(name = "服务器-管理")
public class ServerController {

    @Resource
    ServerService baseService;

    @PreAuthorize("hasAuthority('server:workInfo')")
    @PostMapping("/workInfo")
    @Operation(summary = "服务器运行情况")
    public ApiResultVO<ServerWorkInfoVO> workInfo() {
        return ApiResultVO.okData(baseService.workInfo());
    }

}
