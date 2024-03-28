package com.cmcorg20230301.be.engine.flow.activiti.model.dto;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysActivitiDeployInsertOrUpdateByFileDTO {

    @NotNull
    @Schema(description = "文件")
    private MultipartFile file;

}
