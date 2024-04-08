package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiDeployInsertOrUpdateVO {

    @Schema(description = "部署：id")
    private String deploymentId;

    @Schema(description = "流程定义：key")
    private String processDefinitionKey;

    @Schema(description = "流程定义：id")
    private String processDefinitionId;

}
