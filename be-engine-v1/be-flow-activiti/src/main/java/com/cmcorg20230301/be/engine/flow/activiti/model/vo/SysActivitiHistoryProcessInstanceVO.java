package com.cmcorg20230301.be.engine.flow.activiti.model.vo;

import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiHistoryProcessInstanceVO {

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

    @Schema(description = "流程实例：是否是结束状态")
    protected Boolean ended;

    @Schema(description = "流程实例：参数")
    private Map<String, Object> processVariableMap;

    @Schema(description = "流程实例：id")
    private String id;

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

    @Schema(description = "流程实例：结束时间")
    private Date endTime;

    @Schema(description = "任务：开始到结束的时间，单位：毫秒")
    private Long durationInMillis;

    @Schema(description = "结束活动：id")
    private String endActivityId;

    @Schema(description = "开始活动：id")
    private String startActivityId;

    @Schema(description = "删除原因")
    private String deleteReason;

    @Schema(description = "超级流程实例：id")
    private String superProcessInstanceId;

}
