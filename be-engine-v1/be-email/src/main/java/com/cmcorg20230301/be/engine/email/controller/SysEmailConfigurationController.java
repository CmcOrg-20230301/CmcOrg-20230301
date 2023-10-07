package com.cmcorg20230301.be.engine.email.controller;

import com.cmcorg20230301.be.engine.email.model.dto.SysEmailConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.email.model.entity.SysEmailConfigurationDO;
import com.cmcorg20230301.be.engine.email.service.SysEmailConfigurationService;
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
@RequestMapping(value = "/sys/emailConfiguration")
@Tag(name = "邮箱-配置")
public class SysEmailConfigurationController {

    @Resource
    SysEmailConfigurationService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysEmailConfiguration:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid SysEmailConfigurationInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('sysEmailConfiguration:infoById')")
    public ApiResultVO<SysEmailConfigurationDO> infoById() {
        return ApiResultVO.okData(baseService.infoById());
    }

}
