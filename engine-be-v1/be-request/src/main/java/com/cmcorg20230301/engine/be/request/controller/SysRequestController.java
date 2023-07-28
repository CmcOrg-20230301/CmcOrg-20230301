package com.cmcorg20230301.engine.be.request.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.engine.be.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.cmcorg20230301.engine.be.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.engine.be.request.service.SysRequestService;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/sys/request")
@Tag(name = "请求-管理")
public class SysRequestController {

    @Resource
    SysRequestService baseService;

    @PreAuthorize("hasAuthority('sysRequest:page')")
    @PostMapping("/page")
    @Operation(summary = "分页排序查询")
    public ApiResultVO<Page<SysRequestDO>> myPage(@RequestBody @Valid SysRequestPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @PostMapping("/allAvgPro")
    @Operation(summary = "所有请求的平均耗时-增强：增加筛选项")
    public ApiResultVO<SysRequestAllAvgVO> allAvgPro(@RequestBody @Valid SysRequestPageDTO dto) {
        return ApiResultVO.okData(baseService.allAvgPro(dto));
    }

    @PostMapping("/allAvg")
    @Operation(summary = "所有请求的平均耗时")
    public ApiResultVO<SysRequestAllAvgVO> allAvg() {
        return ApiResultVO.okData(baseService.allAvg());
    }

    @PostMapping("/self/loginRecord")
    @Operation(summary = "当前用户：登录记录")
    public ApiResultVO<Page<SysRequestDO>> selfLoginRecord(@RequestBody @Valid SysRequestSelfLoginRecordPageDTO dto) {
        return ApiResultVO.okData(baseService.selfLoginRecord(dto));
    }

}
