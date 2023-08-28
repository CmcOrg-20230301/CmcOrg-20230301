package com.cmcorg20230301.be.engine.tenant.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantPageDTO extends MyPageDTO {

    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "租户名")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

}
