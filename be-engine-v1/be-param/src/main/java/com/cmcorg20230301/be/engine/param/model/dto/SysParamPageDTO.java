package com.cmcorg20230301.be.engine.param.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysParamPageDTO extends MyTenantPageDTO {

    @Schema(description = "配置名，以 uuid为不变值进行使用，不要用此属性")
    private String name;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
