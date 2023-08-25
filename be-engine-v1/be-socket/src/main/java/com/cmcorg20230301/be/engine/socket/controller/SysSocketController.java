package com.cmcorg20230301.be.engine.socket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.socket.model.dto.SysSocketPageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.be.engine.socket.service.SysSocketService;
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
@RequestMapping(value = "/sys/socket")
@Tag(name = "socket-管理")
public class SysSocketController {

    @Resource
    SysSocketService baseService;

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysSocket:page')")
    public ApiResultVO<Page<SysSocketDO>> myPage(@RequestBody @Valid SysSocketPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "批量：禁用socket")
    @PostMapping("/disableByIdSet")
    @PreAuthorize("hasAuthority('sysSocket:insertOrUpdate')")
    public ApiResultVO<String> disableByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.disableByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "批量：启用socket")
    @PostMapping("/enableByIdSet")
    @PreAuthorize("hasAuthority('sysSocket:insertOrUpdate')")
    public ApiResultVO<String> enableByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.enableByIdSet(notEmptyIdSet));
    }

}
