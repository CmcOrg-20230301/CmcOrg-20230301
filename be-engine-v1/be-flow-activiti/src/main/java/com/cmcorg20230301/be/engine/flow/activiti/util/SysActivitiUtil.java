package com.cmcorg20230301.be.engine.flow.activiti.util;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.cmcorg20230301.be.engine.flow.activiti.model.vo.*;

public class SysActivitiUtil {

    public static SysActivitiDeploymentVO getSysActivitiDeploymentVO(Deployment item) {

        SysActivitiDeploymentVO sysActivitiDeploymentVO = new SysActivitiDeploymentVO();

        sysActivitiDeploymentVO.setId(item.getId());
        sysActivitiDeploymentVO.setName(item.getName());
        sysActivitiDeploymentVO.setCategory(item.getCategory());
        sysActivitiDeploymentVO.setKey(item.getKey());
        sysActivitiDeploymentVO.setTenantId(item.getTenantId());
        sysActivitiDeploymentVO.setDeploymentTime(item.getDeploymentTime());
        sysActivitiDeploymentVO.setVersion(item.getVersion());

        return sysActivitiDeploymentVO;

    }

    public static SysActivitiProcessDefinitionVO getSysActivitiProcessDefinitionVO(ProcessDefinition item) {

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
        sysActivitiProcessDefinitionVO.setSuspended(item.isSuspended());

        return sysActivitiProcessDefinitionVO;

    }

    public static SysActivitiProcessInstanceVO getSysActivitiProcessInstanceVO(ProcessInstance item) {

        SysActivitiProcessInstanceVO sysActivitiProcessInstanceVO = new SysActivitiProcessInstanceVO();

        sysActivitiProcessInstanceVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiProcessInstanceVO.setProcessDefinitionName(item.getProcessDefinitionName());
        sysActivitiProcessInstanceVO.setProcessDefinitionKey(item.getProcessDefinitionKey());
        sysActivitiProcessInstanceVO.setProcessDefinitionVersion(item.getProcessDefinitionVersion());
        sysActivitiProcessInstanceVO.setDeploymentId(item.getDeploymentId());
        sysActivitiProcessInstanceVO.setBusinessKey(item.getBusinessKey());
        sysActivitiProcessInstanceVO.setSuspended(item.isSuspended());
        sysActivitiProcessInstanceVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiProcessInstanceVO.setTenantId(item.getTenantId());
        sysActivitiProcessInstanceVO.setName(item.getName());
        sysActivitiProcessInstanceVO.setDescription(item.getDescription());
        sysActivitiProcessInstanceVO.setStartTime(item.getStartTime());
        sysActivitiProcessInstanceVO.setStartUserId(item.getStartUserId());

        sysActivitiProcessInstanceVO.setId(item.getId());
        sysActivitiProcessInstanceVO.setEnded(item.isEnded());

        return sysActivitiProcessInstanceVO;

    }

    public static SysActivitiTaskVO getSysActivitiTaskVO(Task item) {

        SysActivitiTaskVO sysActivitiTaskVO = new SysActivitiTaskVO();

        sysActivitiTaskVO.setId(item.getId());
        sysActivitiTaskVO.setName(item.getName());
        sysActivitiTaskVO.setDescription(item.getDescription());
        sysActivitiTaskVO.setPriority(item.getPriority());
        sysActivitiTaskVO.setOwner(item.getOwner());
        sysActivitiTaskVO.setAssignee(item.getAssignee());
        sysActivitiTaskVO.setProcessInstanceId(item.getProcessInstanceId());
        sysActivitiTaskVO.setExecutionId(item.getExecutionId());
        sysActivitiTaskVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiTaskVO.setCreateTime(item.getCreateTime());
        sysActivitiTaskVO.setTaskDefinitionKey(item.getTaskDefinitionKey());
        sysActivitiTaskVO.setDueDate(item.getDueDate());
        sysActivitiTaskVO.setCategory(item.getCategory());
        sysActivitiTaskVO.setParentTaskId(item.getParentTaskId());
        sysActivitiTaskVO.setTenantId(item.getTenantId());
        sysActivitiTaskVO.setFormKey(item.getFormKey());
        sysActivitiTaskVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiTaskVO.setClaimTime(item.getClaimTime());
        sysActivitiTaskVO.setBusinessKey(item.getBusinessKey());

        sysActivitiTaskVO.setSuspended(item.isSuspended());

        return sysActivitiTaskVO;

    }

    public static SysActivitiHistoryTaskVO getSysActivitiHistoryTaskVO(HistoricTaskInstance item) {

        SysActivitiHistoryTaskVO sysActivitiHistoryTaskVO = new SysActivitiHistoryTaskVO();

        sysActivitiHistoryTaskVO.setId(item.getId());
        sysActivitiHistoryTaskVO.setName(item.getName());
        sysActivitiHistoryTaskVO.setDescription(item.getDescription());
        sysActivitiHistoryTaskVO.setPriority(item.getPriority());
        sysActivitiHistoryTaskVO.setOwner(item.getOwner());
        sysActivitiHistoryTaskVO.setAssignee(item.getAssignee());
        sysActivitiHistoryTaskVO.setProcessInstanceId(item.getProcessInstanceId());
        sysActivitiHistoryTaskVO.setExecutionId(item.getExecutionId());
        sysActivitiHistoryTaskVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiHistoryTaskVO.setCreateTime(item.getCreateTime());
        sysActivitiHistoryTaskVO.setTaskDefinitionKey(item.getTaskDefinitionKey());
        sysActivitiHistoryTaskVO.setDueDate(item.getDueDate());
        sysActivitiHistoryTaskVO.setCategory(item.getCategory());
        sysActivitiHistoryTaskVO.setParentTaskId(item.getParentTaskId());
        sysActivitiHistoryTaskVO.setTenantId(item.getTenantId());
        sysActivitiHistoryTaskVO.setFormKey(item.getFormKey());
        sysActivitiHistoryTaskVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiHistoryTaskVO.setClaimTime(item.getClaimTime());
        sysActivitiHistoryTaskVO.setBusinessKey(item.getBusinessKey());

        sysActivitiHistoryTaskVO.setStartTime(item.getStartTime());
        sysActivitiHistoryTaskVO.setEndTime(item.getEndTime());
        sysActivitiHistoryTaskVO.setDurationInMillis(item.getDurationInMillis());
        sysActivitiHistoryTaskVO.setWorkTimeInMillis(item.getWorkTimeInMillis());

        sysActivitiHistoryTaskVO.setDeleteReason(item.getDeleteReason());

        return sysActivitiHistoryTaskVO;

    }

    public static SysActivitiHistoryProcessInstanceVO
        getSysActivitiHistoryProcessInstanceVO(HistoricProcessInstance item) {

        SysActivitiHistoryProcessInstanceVO sysActivitiHistoryProcessInstanceVO =
            new SysActivitiHistoryProcessInstanceVO();

        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionId(item.getProcessDefinitionId());
        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionName(item.getProcessDefinitionName());
        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionKey(item.getProcessDefinitionKey());
        sysActivitiHistoryProcessInstanceVO.setProcessDefinitionVersion(item.getProcessDefinitionVersion());
        sysActivitiHistoryProcessInstanceVO.setDeploymentId(item.getDeploymentId());
        sysActivitiHistoryProcessInstanceVO.setBusinessKey(item.getBusinessKey());
        sysActivitiHistoryProcessInstanceVO.setProcessVariableMap(item.getProcessVariables());
        sysActivitiHistoryProcessInstanceVO.setTenantId(item.getTenantId());
        sysActivitiHistoryProcessInstanceVO.setName(item.getName());
        sysActivitiHistoryProcessInstanceVO.setDescription(item.getDescription());
        sysActivitiHistoryProcessInstanceVO.setStartTime(item.getStartTime());
        sysActivitiHistoryProcessInstanceVO.setStartUserId(item.getStartUserId());

        sysActivitiHistoryProcessInstanceVO.setId(item.getId());

        sysActivitiHistoryProcessInstanceVO.setEndTime(item.getEndTime());
        sysActivitiHistoryProcessInstanceVO.setDurationInMillis(item.getDurationInMillis());
        sysActivitiHistoryProcessInstanceVO.setEndActivityId(item.getEndActivityId());
        sysActivitiHistoryProcessInstanceVO.setStartActivityId(item.getStartActivityId());
        sysActivitiHistoryProcessInstanceVO.setDeleteReason(item.getDeleteReason());
        sysActivitiHistoryProcessInstanceVO.setSuperProcessInstanceId(item.getSuperProcessInstanceId());

        return sysActivitiHistoryProcessInstanceVO;

    }

}
