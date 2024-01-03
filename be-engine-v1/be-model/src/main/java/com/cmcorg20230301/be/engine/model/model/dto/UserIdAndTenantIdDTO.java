package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserIdAndTenantIdDTO {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "租户主键 id")
    private Long tenantId;

}
