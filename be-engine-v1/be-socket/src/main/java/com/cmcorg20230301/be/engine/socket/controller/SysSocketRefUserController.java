package com.cmcorg20230301.be.engine.socket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.socket.model.dto.SysSocketRefUserPageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.be.engine.socket.service.SysSocketRefUserService;
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
@RequestMapping(value = "/sys/socketRefUser")
@Tag(name = "基础-socket-用户管理")
public class SysSocketRefUserController {

    @Resource
    SysSocketRefUserService baseService;

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysSocketRefUser:page')")
    public ApiResultVO<Page<SysSocketRefUserDO>> myPage(@RequestBody @Valid SysSocketRefUserPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

    @Operation(summary = "批量：下线用户")
    @PostMapping("/offlineByIdSet")
    @PreAuthorize("hasAuthority('sysSocketRefUser:insertOrUpdate')")
    public ApiResultVO<String> offlineByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.okMsg(baseService.offlineByIdSet(notEmptyIdSet));
    }

}
