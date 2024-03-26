package com.cmcorg20230301.be.engine.security.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmcorg20230301.be.engine.security.model.dto.SysLogPushDTO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.service.SysLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "基础-日志-管理")
@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    @Resource
    SysLogService baseService;

    @PostMapping(value = "/push")
    @Operation(summary = "新增：日志记录")
    public ApiResultVO<String> push(@RequestBody @Valid SysLogPushDTO dto) {
        return ApiResultVO.okMsg(baseService.push(dto));
    }

}
