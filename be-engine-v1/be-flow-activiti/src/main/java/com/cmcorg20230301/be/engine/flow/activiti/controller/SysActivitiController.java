package com.cmcorg20230301.be.engine.flow.activiti.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.activiti.engine.repository.Deployment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiDeployInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiDeployPageDTO;
import com.cmcorg20230301.be.engine.flow.activiti.service.SysActivitiService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/sys/activiti")
@RestController
@Tag(name = "基础-工作流-activiti-管理")
public class SysActivitiController {

    @Resource
    SysActivitiService baseService;

    @Operation(summary = "部署-新增/修改")
    @PostMapping("/deploy/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysActiviti:deployInsertOrUpdate')")
    public ApiResultVO<String> deployInsertOrUpdate(@RequestBody @Valid SysActivitiDeployInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.deployInsertOrUpdate(dto));
    }

    @Operation(summary = "部署-分页排序查询")
    @PostMapping("/deploy/page")
    @PreAuthorize("hasAuthority('sysActiviti:deployPage')")
    public ApiResultVO<Page<Deployment>> deployPage(@RequestBody @Valid SysActivitiDeployPageDTO dto) {
        return ApiResultVO.okData(baseService.deployPage(dto));
    }

    @Operation(summary = "部署-批量删除")
    @PostMapping("/deploy/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:deployDeleteByIdSet')")
    public ApiResultVO<String> deployDeleteByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.deployDeleteByIdSet(notEmptyStringSet));
    }

}
