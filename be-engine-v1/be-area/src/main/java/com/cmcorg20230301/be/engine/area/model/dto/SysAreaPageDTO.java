package com.cmcorg20230301.be.engine.area.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysAreaPageDTO extends MyTenantPageDTO {

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "区域名")
    private String name;

    @Schema(description = "备注")
    private String remark;

}
