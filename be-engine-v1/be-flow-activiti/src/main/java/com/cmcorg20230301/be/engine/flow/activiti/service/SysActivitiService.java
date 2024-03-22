package com.cmcorg20230301.be.engine.flow.activiti.service;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiProcessDefinitionVO;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankString;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;

public interface SysActivitiService {

    String deployInsertOrUpdate(SysActivitiDeployInsertOrUpdateDTO dto);

    Page<Deployment> deployPage(SysActivitiDeployPageDTO dto);

    String deployDeleteByIdSet(NotEmptyStringSet notEmptyStringSet);

    Page<SysActivitiProcessDefinitionVO> processDefinitionPage(SysActivitiProcessDefinitionPageDTO dto);

    String processInstanceInsertOrUpdate(SysActivitiProcessInstanceInsertOrUpdateDTO dto);

    String processInstanceInsertOrUpdateByKey(SysActivitiProcessInstanceInsertOrUpdateByKeyDTO dto);

    ProcessInstance processInstanceInfoById(NotBlankString notBlankString);

    Page<ProcessInstance> processInstancePage(SysActivitiProcessInstancePageDTO dto);

    String processInstanceSuspendByIdSet(NotEmptyStringSet notEmptyStringSet);

    String processInstanceActiveByIdSet(NotEmptyStringSet notEmptyStringSet);

    String processInstanceDeleteByIdSet(NotEmptyStringSet notEmptyStringSet);

    Page<Task> taskPage(SysActivitiTaskPageDTO dto);

    String taskClaimByIdSet(NotEmptyStringSet notEmptyStringSet);

    String taskReturnByIdSet(NotEmptyStringSet notEmptyStringSet);

    String taskCompleteByIdSet(NotEmptyStringSet notEmptyStringSet);

}
