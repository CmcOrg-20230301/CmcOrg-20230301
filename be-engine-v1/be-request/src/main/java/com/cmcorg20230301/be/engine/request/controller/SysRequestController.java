package com.cmcorg20230301.be.engine.request.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.cmcorg20230301.be.engine.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.be.engine.request.service.SysRequestService;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/request")
@Tag(name = "基础-请求-管理")
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

    @PostMapping("/self/loginRecord")
    @Operation(summary = "当前用户：登录记录")
    public ApiResultVO<Page<SysRequestDO>> selfLoginRecord(
        @RequestBody @Valid SysRequestSelfLoginRecordPageDTO dto) {
        return ApiResultVO.okData(baseService.selfLoginRecord(dto));
    }

}
