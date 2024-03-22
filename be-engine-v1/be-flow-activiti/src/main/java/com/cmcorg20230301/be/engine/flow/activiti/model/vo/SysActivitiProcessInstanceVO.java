package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiProcessInstanceVO {

    @Schema(description = "流程定义：id")
    private String processDefinitionId;

    @Schema(description = "流程定义：名称")
    private String processDefinitionName;

    @Schema(description = "流程定义：key")
    private String processDefinitionKey;

    @Schema(description = "流程定义：版本号")
    private Integer processDefinitionVersion;

    @Schema(description = "部署：id")
    private String deploymentId;

    @Schema(description = "流程实例：业务key")
    private String businessKey;

    @Schema(description = "流程实例：是否是暂停状态")
    protected Boolean suspended;

    @Schema(description = "流程实例：参数")
    private Map<String, Object> processVariableMap;

    @Schema(description = "流程实例：租户 id")
    private String tenantId;

    @Schema(description = "流程实例：名称")
    private String name;

    @Schema(description = "流程实例：描述")
    private String description;

    @Schema(description = "流程实例：开始时间")
    private Date startTime;

    @Schema(description = "流程实例：开始用户主键 id")
    private String startUserId;

}
