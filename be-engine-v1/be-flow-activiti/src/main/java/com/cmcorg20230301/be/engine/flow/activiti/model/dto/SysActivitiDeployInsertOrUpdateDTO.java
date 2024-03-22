package com.cmcorg20230301.be.engine.flow.activiti.model.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiDeployInsertOrUpdateDTO {

    @NotBlank
    @Schema(description = "获取：部署文件的url")
    private String url;

}
