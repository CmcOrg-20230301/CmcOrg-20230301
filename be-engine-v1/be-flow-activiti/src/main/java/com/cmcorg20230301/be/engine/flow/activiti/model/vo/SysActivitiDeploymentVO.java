package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiDeploymentVO {

    @Schema(description = "部署：id")
    private String id;

    @Schema(description = "部署：名称")
    private String name;

    @Schema(description = "部署：分类")
    private String category;

    @Schema(description = "部署：key，格式：流程定义 id + # + 流程定义 key")
    private String key;

    @Schema(description = "部署：租户id")
    private String tenantId;

    @Schema(description = "部署：创建时间")
    private Date deploymentTime;

    @Schema(description = "部署：版本号")
    private Integer version;

    @Schema(description = "流程定义：key")
    private String processDefinitionKey;

    @Schema(description = "流程定义：id")
    private String processDefinitionId;

}
