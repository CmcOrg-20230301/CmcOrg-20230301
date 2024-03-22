package com.cmcorg20230301.be.engine.flow.activiti.util;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiDeploymentVO;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiProcessDefinitionVO;
import com.cmcorg20230301.be.engine.flow.activiti.model.vo.SysActivitiProcessInstanceVO;

public class SysActivitiUtil {

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
        sysActivitiProcessDefinitionVO.setSuspensionState(item.isSuspended());

        return sysActivitiProcessDefinitionVO;

    }

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

    public static SysActivitiProcessInstanceVO getSysActivitiProcessInstanceVO(ProcessInstance item) {

        SysActivitiProcessInstanceVO sysActivitiProcessInstanceVO = new SysActivitiProcessInstanceVO();

        return sysActivitiProcessInstanceVO;

    }

}
