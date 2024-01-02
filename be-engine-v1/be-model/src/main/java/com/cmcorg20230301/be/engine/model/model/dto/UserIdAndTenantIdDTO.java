package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserIdAndTenantIdDTO {

    @Min(0)
    @NotNull
    @Schema(description = "用户主键 id")
    private Long userId;

    @Min(0)
    @NotNull
    @Schema(description = "租户主键 id")
    private Long tenantId;

}
