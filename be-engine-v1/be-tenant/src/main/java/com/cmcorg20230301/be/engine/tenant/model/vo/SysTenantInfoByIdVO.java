package com.cmcorg20230301.be.engine.tenant.model.vo;

import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantInfoByIdVO extends SysTenantDO {

    @Schema(description = "用户 idSet")
    private Set<Long> userIdSet;

}
