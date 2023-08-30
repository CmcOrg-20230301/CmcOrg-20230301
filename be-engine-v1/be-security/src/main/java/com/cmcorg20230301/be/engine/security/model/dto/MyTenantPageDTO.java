package com.cmcorg20230301.be.engine.security.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyTenantPageDTO extends MyPageDTO {

    @Schema(description = "租户 idSet")
    private Set<Long> tenantIdSet;

}
