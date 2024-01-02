package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserIdAndTenantIdAndLongSetDTO extends UserIdAndTenantIdDTO {

    @Schema(description = "值 set，备注：可以为空")
    private Set<Long> valueSet;

}
