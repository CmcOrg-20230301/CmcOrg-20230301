package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiProcessDefinitionVO {

    @Schema(description = "流程定义：id")
    private String id;

    @Schema(description = "流程定义：名称")
    private String name;

    @Schema(description = "流程定义：描述")
    private String description;

    @Schema(description = "流程定义：key")
    private String key;

    @Schema(description = "流程定义：版本号")
    private Integer version;

    @Schema(description = "流程定义：分类")
    private String category;

    @Schema(description = "部署：id")
    private String deploymentId;

    @Schema(description = "部署文件：名称")
    private String resourceName;

    @Schema(description = "流程定义：租户id")
    private String tenantId;

    @Schema(description = "流程定义：是否是暂停状态")
    protected Boolean suspensionState;

}
