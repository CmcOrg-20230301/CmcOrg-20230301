package com.cmcorg20230301.be.engine.security.model.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysLogPushDTO {

    @NotBlank
    @Schema(description = "日志")
    private String log;

}
