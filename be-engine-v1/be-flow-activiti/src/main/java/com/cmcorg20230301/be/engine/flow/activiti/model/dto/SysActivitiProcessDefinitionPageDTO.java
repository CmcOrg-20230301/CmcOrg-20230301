package com.cmcorg20230301.be.engine.flow.activiti.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysActivitiProcessDefinitionPageDTO extends MyTenantPageDTO {

    @Schema(description = "部署：id")
    private String deploymentId;

    @Schema(description = "流程定义：id")
    private String id;

    @Schema(description = "流程定义：key")
    private String key;

    @Schema(description = "流程定义：名称")
    private String name;

    @Schema(description = "是否只查询：数量，默认：false")
    private Boolean onlyQueryCount;

}
