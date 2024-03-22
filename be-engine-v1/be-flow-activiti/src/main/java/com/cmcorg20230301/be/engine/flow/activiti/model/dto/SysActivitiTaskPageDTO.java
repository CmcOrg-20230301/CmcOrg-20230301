package com.cmcorg20230301.be.engine.flow.activiti.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysActivitiTaskPageDTO extends MyTenantPageDTO {

    @Schema(description = "流程定义：id")
    private String processDefinitionId;

    @Schema(description = "流程定义：key")
    private String processDefinitionKey;

    @Schema(description = "流程实例：id")
    private String processInstanceId;

    @Schema(description = "流程实例：业务key")
    private String processInstanceBusinessKey;

    @Schema(description = "任务：id")
    private String taskId;

}
