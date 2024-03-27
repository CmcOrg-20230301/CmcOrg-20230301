package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiHistoryTaskVO {

    @Schema(description = "任务：id")
    private String id;

    @Schema(description = "任务：名称")
    private String name;

    @Schema(description = "任务：描述")
    private String description;

    @Schema(description = "任务：优先级")
    private Integer priority;

    @Schema(description = "任务：拥有者")
    private String owner;

    @Schema(description = "任务：执行者")
    private String assignee;

    @Schema(description = "流程实例：id")
    private String processInstanceId;

    @Schema(description = "执行器：id")
    private String executionId;

    @Schema(description = "流程定义：id")
    private String processDefinitionId;

    @Schema(description = "任务：创建时间")
    private Date createTime;

    @Schema(description = "任务定义：id")
    private String taskDefinitionKey;

    @Schema(description = "任务：到期时间")
    private Date dueDate;

    @Schema(description = "任务：分类")
    private String category;

    @Schema(description = "任务：父级任务 id")
    private String parentTaskId;

    @Schema(description = "任务：租户 id")
    private String tenantId;

    @Schema(description = "任务：表单key")
    private String formKey;

    @Schema(description = "任务：参数")
    private Map<String, Object> processVariableMap;

    @Schema(description = "任务：执行者接受任务的时间")
    private Date claimTime;

    @Schema(description = "任务：业务key")
    private String businessKey;

    @Schema(description = "任务：开始时间")
    private Date startTime;

    @Schema(description = "任务：结束时间")
    private Date endTime;

    @Schema(description = "任务：开始到结束的时间，单位：毫秒")
    private Long durationInMillis;

    @Schema(description = "任务：接受到结束的时间，单位：毫秒")
    private Long workTimeInMillis;

}
