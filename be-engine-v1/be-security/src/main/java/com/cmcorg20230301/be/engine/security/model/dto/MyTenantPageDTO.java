package com.cmcorg20230301.be.engine.security.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页参数，查询所有：pageSize = -1，默认：current = 1，pageSize = 10")
public class MyTenantPageDTO extends MyPageDTO {

    @Schema(description = "租户 id")
    private Long tenantId;

    @Schema(description = "租户 idSet")
    private Set<Long> tenantIdSet;

}
