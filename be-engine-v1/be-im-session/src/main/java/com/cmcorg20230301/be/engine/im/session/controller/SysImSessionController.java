package com.cmcorg20230301.be.engine.im.session.controller;

import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionInsertOrUpDateDTO;
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
@Tag(name = "即时通讯-会话-管理")
public class SysImSessionController {

    @Resource
    SysImSessionService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysArea:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysImSessionInsertOrUpDateDTO dto) {
        return ApiResultVO.okData(baseService.insertOrUpdate(dto));
    }

}
