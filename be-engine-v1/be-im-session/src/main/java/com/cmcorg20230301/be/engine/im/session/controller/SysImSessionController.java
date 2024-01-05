package com.cmcorg20230301.be.engine.im.session.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionInsertOrUpDateDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
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
@RequestMapping("/sys/im/session")
@Tag(name = "基础-即时通讯-会话-管理")
public class SysImSessionController {

    @Resource
    SysImSessionService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysImSession:insertOrUpdate')")
    public ApiResultVO<Long> insertOrUpdate(@RequestBody @Valid SysImSessionInsertOrUpDateDTO dto) {
        return ApiResultVO.okData(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sysImSession:page')")
    public ApiResultVO<Page<SysImSessionDO>> myPage(@RequestBody @Valid SysImSessionPageDTO dto) {
        return ApiResultVO.okData(baseService.myPage(dto));
    }

}
