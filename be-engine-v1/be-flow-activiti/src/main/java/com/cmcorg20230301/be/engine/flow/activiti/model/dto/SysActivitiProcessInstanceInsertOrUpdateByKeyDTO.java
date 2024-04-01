package com.cmcorg20230301.be.engine.flow.activiti.model.dto;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiProcessInstanceInsertOrUpdateByKeyDTO {

    @NotBlank
    @Schema(description = "流程定义：key")
    private String processDefinitionKey;

    @NotBlank
    @Schema(description = "业务：key")
    private String businessKey;

    @Schema(description = "参数：map")
    private Map<String, Object> variableMap;

}