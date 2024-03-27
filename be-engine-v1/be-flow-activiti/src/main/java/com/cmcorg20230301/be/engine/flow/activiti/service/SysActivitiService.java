package com.cmcorg20230301.be.engine.flow.activiti.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.*;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.*;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankString;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;

public interface SysActivitiService {

    String deployInsertOrUpdate(SysActivitiDeployInsertOrUpdateDTO dto);

    Page<SysActivitiDeploymentVO> deployPage(SysActivitiDeployPageDTO dto);

    String deployDeleteByIdSet(NotEmptyStringSet notEmptyStringSet);

    Page<SysActivitiProcessDefinitionVO> processDefinitionPage(SysActivitiProcessDefinitionPageDTO dto);

    String processInstanceInsertOrUpdate(SysActivitiProcessInstanceInsertOrUpdateDTO dto);

    SysActivitiProcessInstanceVO processInstanceInfoById(NotBlankString notBlankString);

    Page<SysActivitiProcessInstanceVO> processInstancePage(SysActivitiProcessInstancePageDTO dto);

    String processInstanceSuspendByIdSet(NotEmptyStringSet notEmptyStringSet);

    String processInstanceActiveByIdSet(NotEmptyStringSet notEmptyStringSet);

    String processInstanceDeleteByIdSet(NotEmptyStringSet notEmptyStringSet);

    Page<SysActivitiTaskVO> taskPage(SysActivitiTaskPageDTO dto);

    String taskClaimByIdSet(NotEmptyStringSet notEmptyStringSet);

    String taskReturnByIdSet(NotEmptyStringSet notEmptyStringSet);

    String taskCompleteByIdSet(NotEmptyStringSet notEmptyStringSet);

    Page<SysActivitiHistoryTaskVO> historyTaskPage(SysActivitiHistoryTaskPageDTO dto);

}
