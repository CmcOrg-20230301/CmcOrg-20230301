package com.cmcorg20230301.be.engine.flow.activiti.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiProcessDefinitionVO;
import com.cmcorg20230301.be.engine.flow.activiti.service.SysActivitiService;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankString;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.util.UserUtil;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

@Service
public class SysActivitiServiceImpl implements SysActivitiService {

    @Resource
    RepositoryService repositoryService;

    @Resource
    RuntimeService runtimeService;

    @Resource
    TaskService taskService;

    /**
     * 部署-新增/修改
     */
    @Override
    public String deployInsertOrUpdate(SysActivitiDeployInsertOrUpdateDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        String url = dto.getUrl();

        String fileName = FileNameUtil.getName(url);

        byte[] downloadByteArr = HttpUtil.downloadBytes(url);

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addBytes(fileName, downloadByteArr);

        deploymentBuilder.name(fileName);

        deploymentBuilder.tenantId(tenantId.toString());

        Deployment deployment = deploymentBuilder.deploy();

        return deployment.getId();

    }

    /**
     * 部署-分页排序查询
     */
    @Override
    public Page<Deployment> deployPage(SysActivitiDeployPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();

        deploymentQuery.deploymentTenantId(tenantId.toString());

        if (StrUtil.isNotBlank(dto.getId())) {
            deploymentQuery.deploymentId(dto.getId());
        }

        if (StrUtil.isNotBlank(dto.getName())) {
            deploymentQuery.deploymentNameLike(dto.getName());
        }

        long count = deploymentQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        deploymentQuery.orderByDeploymentId().desc();

        List<Deployment> deploymentList = deploymentQuery.listPage((int)firstResult, (int)page.getSize());

        return new Page<Deployment>().setTotal(count).setRecords(deploymentList);

    }

    /**
     * 部署-批量删除
     */
    @Override
    public String deployDeleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String deploymentId : notEmptyStringSet.getIdSet()) {

            // 避免：出现删除不属于自己租户的部署
            repositoryService.createDeploymentQuery().deploymentId(deploymentId).deploymentTenantId(tenantId)
                .singleResult();

            repositoryService.deleteDeployment(deploymentId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 流程定义-分页排序查询
     */
    @Override
    public Page<SysActivitiProcessDefinitionVO> processDefinitionPage(SysActivitiProcessDefinitionPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        processDefinitionQuery.processDefinitionTenantId(tenantId.toString());

        if (StrUtil.isNotBlank(dto.getDeploymentId())) {
            processDefinitionQuery.deploymentId(dto.getDeploymentId());
        }

        if (StrUtil.isNotBlank(dto.getId())) {
            processDefinitionQuery.processDefinitionId(dto.getId());
        }

        if (StrUtil.isNotBlank(dto.getKey())) {
            processDefinitionQuery.processDefinitionKeyLike(dto.getKey());
        }

        if (StrUtil.isNotBlank(dto.getName())) {
            processDefinitionQuery.processDefinitionNameLike(dto.getName());
        }

        long count = processDefinitionQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        processDefinitionQuery.orderByProcessDefinitionId().desc();

        List<ProcessDefinition> processDefinitionList =
            processDefinitionQuery.listPage((int)firstResult, (int)page.getSize());

        List<SysActivitiProcessDefinitionVO> list = new ArrayList<>(processDefinitionList.size());

        for (ProcessDefinition item : processDefinitionList) {

            SysActivitiProcessDefinitionVO sysActivitiProcessDefinitionVO = new SysActivitiProcessDefinitionVO();

            sysActivitiProcessDefinitionVO.setId(item.getId());
            sysActivitiProcessDefinitionVO.setName(item.getName());
            sysActivitiProcessDefinitionVO.setDescription(item.getDescription());
            sysActivitiProcessDefinitionVO.setKey(item.getKey());
            sysActivitiProcessDefinitionVO.setVersion(item.getVersion());
            sysActivitiProcessDefinitionVO.setCategory(item.getCategory());
            sysActivitiProcessDefinitionVO.setDeploymentId(item.getDeploymentId());
            sysActivitiProcessDefinitionVO.setResourceName(item.getResourceName());
            sysActivitiProcessDefinitionVO.setTenantId(item.getTenantId());
            sysActivitiProcessDefinitionVO.setSuspensionState(item.isSuspended());

            list.add(sysActivitiProcessDefinitionVO);

        }

        return new Page<SysActivitiProcessDefinitionVO>().setTotal(count).setRecords(list);

    }

    /**
     * 流程实例-新增/修改
     */
    @Override
    public String processInstanceInsertOrUpdate(SysActivitiProcessInstanceInsertOrUpdateDTO dto) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(dto.getProcessDefinitionId(),
            dto.getBusinessKey(), dto.getVariableMap());

        return processInstance.getProcessInstanceId();

    }

    /**
     * 流程实例-通过主键id，查看详情
     */
    @Override
    public ProcessInstance processInstanceInfoById(NotBlankString notBlankString) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        return runtimeService.createProcessInstanceQuery().processInstanceId(notBlankString.getValue())
            .processInstanceTenantId(tenantId.toString()).singleResult();

    }

    /**
     * 流程实例-分页排序查询
     */
    @Override
    public Page<ProcessInstance> processInstancePage(SysActivitiProcessInstancePageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();

        processInstanceQuery.processInstanceTenantId(tenantId.toString());

        if (StrUtil.isNotBlank(dto.getProcessDefinitionId())) {
            processInstanceQuery.processDefinitionId(dto.getProcessDefinitionId());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionKey())) {
            processInstanceQuery.processDefinitionKey(dto.getProcessDefinitionKey());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceId())) {
            processInstanceQuery.processInstanceId(dto.getProcessInstanceId());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceBusinessKey())) {
            processInstanceQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }

        long count = processInstanceQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        processInstanceQuery.orderByProcessInstanceId().desc();

        List<ProcessInstance> processDefinitionList =
            processInstanceQuery.listPage((int)firstResult, (int)page.getSize());

        return new Page<ProcessInstance>().setTotal(count).setRecords(processDefinitionList);

    }

    /**
     * 流程实例-批量挂起
     */
    @Override
    public String processInstanceSuspendByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String processInstanceId : notEmptyStringSet.getIdSet()) {

            // 避免：出现挂起不属于自己租户的流程实例
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .processInstanceTenantId(tenantId).singleResult();

            runtimeService.suspendProcessInstanceById(processInstanceId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 流程实例-批量激活
     */
    @Override
    public String processInstanceActiveByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String processInstanceId : notEmptyStringSet.getIdSet()) {

            // 避免：出现激活不属于自己租户的流程实例
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .processInstanceTenantId(tenantId).singleResult();

            runtimeService.activateProcessInstanceById(processInstanceId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 流程实例-批量删除
     */
    @Override
    public String processInstanceDeleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String processInstanceId : notEmptyStringSet.getIdSet()) {

            // 避免：出现删除不属于自己租户的流程实例
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .processInstanceTenantId(tenantId).singleResult();

            runtimeService.deleteProcessInstance(processInstanceId, null);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 任务-分页排序查询
     */
    @Override
    public Page<Task> taskPage(SysActivitiTaskPageDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        TaskQuery taskQuery = taskService.createTaskQuery();

        taskQuery.taskTenantId(tenantId.toString());

        if (StrUtil.isNotBlank(dto.getProcessDefinitionId())) {
            taskQuery.processDefinitionId(dto.getProcessDefinitionId());
        }

        if (StrUtil.isNotBlank(dto.getProcessDefinitionKey())) {
            taskQuery.processDefinitionKey(dto.getProcessDefinitionKey());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceId())) {
            taskQuery.processInstanceId(dto.getProcessInstanceId());
        }

        if (StrUtil.isNotBlank(dto.getProcessInstanceBusinessKey())) {
            taskQuery.processInstanceBusinessKey(dto.getProcessInstanceBusinessKey());
        }

        if (StrUtil.isNotBlank(dto.getTaskId())) {
            taskQuery.taskId(dto.getTaskId());
        }

        long count = taskQuery.count();

        if (count == 0) {
            return new Page<>();
        }

        Page<Deployment> page = dto.page(true);

        long firstResult = (page.getCurrent() - 1) * page.getSize();

        taskQuery.orderByTaskId().desc();

        List<Task> taskList = taskQuery.listPage((int)firstResult, (int)page.getSize());

        return new Page<Task>().setTotal(count).setRecords(taskList);

    }

    /**
     * 任务-批量接受
     */
    @Override
    public String taskClaimByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String userId = UserUtil.getCurrentUserId().toString();

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String taskId : notEmptyStringSet.getIdSet()) {

            // 避免：出现接受不属于自己租户的任务
            taskService.createTaskQuery().taskId(taskId).taskTenantId(tenantId).singleResult();

            taskService.claim(taskId, userId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 任务-批量归还
     */
    @Override
    public String taskReturnByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String taskId : notEmptyStringSet.getIdSet()) {

            // 避免：出现归还不属于自己租户的任务
            taskService.createTaskQuery().taskId(taskId).taskTenantId(tenantId).singleResult();

            taskService.unclaim(taskId);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 任务-批量完成
     */
    @Override
    public String taskCompleteByIdSet(NotEmptyStringSet notEmptyStringSet) {

        String tenantId = UserUtil.getCurrentTenantIdDefault().toString();

        for (String taskId : notEmptyStringSet.getIdSet()) {

            // 避免：出现完成不属于自己租户的任务
            taskService.createTaskQuery().taskId(taskId).taskTenantId(tenantId).singleResult();

            taskService.complete(taskId, notEmptyStringSet.getVariableMap());

        }

        return BaseBizCodeEnum.OK;

    }

}
