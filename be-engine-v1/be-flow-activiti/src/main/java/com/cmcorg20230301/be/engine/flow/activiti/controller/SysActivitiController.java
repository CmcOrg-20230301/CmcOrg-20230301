package com.cmcorg20230301.be.engine.flow.activiti.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiDeploymentVO;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiProcessDefinitionVO;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiProcessInstanceVO;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiTaskVO;
import com.cmcorg20230301.be.engine.flow.activiti.service.SysActivitiService;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankString;
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
    public ApiResultVO<Page<SysActivitiDeploymentVO>> deployPage(@RequestBody @Valid SysActivitiDeployPageDTO dto) {
        return ApiResultVO.okData(baseService.deployPage(dto));
    }

    @Operation(summary = "部署-批量删除")
    @PostMapping("/deploy/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:deployDeleteByIdSet')")
    public ApiResultVO<String> deployDeleteByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.deployDeleteByIdSet(notEmptyStringSet));
    }

    @Operation(summary = "流程定义-分页排序查询")
    @PostMapping("/processDefinition/page")
    @PreAuthorize("hasAuthority('sysActiviti:processDefinitionPage')")
    public ApiResultVO<Page<SysActivitiProcessDefinitionVO>>
        processDefinitionPage(@RequestBody @Valid SysActivitiProcessDefinitionPageDTO dto) {
        return ApiResultVO.okData(baseService.processDefinitionPage(dto));
    }

    @Operation(summary = "流程实例-新增/修改")
    @PostMapping("/processInstance/insertOrUpdate")
    @PreAuthorize("hasAuthority('sysActiviti:processInstanceInsertOrUpdate')")
    public ApiResultVO<String>
        processInstanceInsertOrUpdate(@RequestBody @Valid SysActivitiProcessInstanceInsertOrUpdateDTO dto) {
        return ApiResultVO.okMsg(baseService.processInstanceInsertOrUpdate(dto));
    }

    @Operation(summary = "流程实例-通过主键id，查看详情")
    @PostMapping("/processInstance/infoById")
    @PreAuthorize("hasAuthority('sysActiviti:processInstanceInfoById')")
    public ApiResultVO<SysActivitiProcessInstanceVO>
        processInstanceInfoById(@RequestBody @Valid NotBlankString notBlankString) {
        return ApiResultVO.okData(baseService.processInstanceInfoById(notBlankString));
    }

    @Operation(summary = "流程实例-分页排序查询")
    @PostMapping("/processInstance/page")
    @PreAuthorize("hasAuthority('sysActiviti:processInstancePage')")
    public ApiResultVO<Page<SysActivitiProcessInstanceVO>>
        processInstancePage(@RequestBody @Valid SysActivitiProcessInstancePageDTO dto) {
        return ApiResultVO.okData(baseService.processInstancePage(dto));
    }

    @Operation(summary = "流程实例-批量挂起")
    @PostMapping("/processInstance/suspendByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:processInstanceSuspendByIdSet')")
    public ApiResultVO<String> processInstanceSuspendByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.processInstanceSuspendByIdSet(notEmptyStringSet));
    }

    @Operation(summary = "流程实例-批量激活")
    @PostMapping("/processInstance/activeByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:processInstanceActiveByIdSet')")
    public ApiResultVO<String> processInstanceActiveByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.processInstanceActiveByIdSet(notEmptyStringSet));
    }

    @Operation(summary = "流程实例-批量删除")
    @PostMapping("/processInstance/deleteByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:processInstanceDeleteByIdSet')")
    public ApiResultVO<String> processInstanceDeleteByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.processInstanceDeleteByIdSet(notEmptyStringSet));
    }

    @Operation(summary = "任务-分页排序查询")
    @PostMapping("/task/page")
    @PreAuthorize("hasAuthority('sysActiviti:taskPage')")
    public ApiResultVO<Page<SysActivitiTaskVO>> taskPage(@RequestBody @Valid SysActivitiTaskPageDTO dto) {
        return ApiResultVO.okData(baseService.taskPage(dto));
    }

    @Operation(summary = "任务-批量接受")
    @PostMapping("/task/claimByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:taskClaimByIdSet')")
    public ApiResultVO<String> taskClaimByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.taskClaimByIdSet(notEmptyStringSet));
    }

    @Operation(summary = "任务-批量归还")
    @PostMapping("/task/returnByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:taskReturnByIdSet')")
    public ApiResultVO<String> taskReturnByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.taskReturnByIdSet(notEmptyStringSet));
    }

    @Operation(summary = "任务-批量完成")
    @PostMapping("/task/completeByIdSet")
    @PreAuthorize("hasAuthority('sysActiviti:taskCompleteByIdSet')")
    public ApiResultVO<String> taskCompleteByIdSet(@RequestBody @Valid NotEmptyStringSet notEmptyStringSet) {
        return ApiResultVO.okMsg(baseService.taskCompleteByIdSet(notEmptyStringSet));
    }

}
