package com.cmcorg20230301.be.engine.flow.activiti.service;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiDeployInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiDeployPageDTO;
import com.cmcorg20230301.be.engine.flow.activiti.model.dto.SysActivitiProcessDefinitionPageDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyStringSet;

public interface SysActivitiService {

    String deployInsertOrUpdate(SysActivitiDeployInsertOrUpdateDTO dto);

    Page<Deployment> deployPage(SysActivitiDeployPageDTO dto);

    String deployDeleteByIdSet(NotEmptyStringSet notEmptyStringSet);

    Page<ProcessDefinition> processDefinitionPage(SysActivitiProcessDefinitionPageDTO dto);

}
