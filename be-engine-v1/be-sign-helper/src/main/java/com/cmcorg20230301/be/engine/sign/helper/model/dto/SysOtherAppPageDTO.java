package com.cmcorg20230301.be.engine.sign.helper.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.sign.helper.model.enums.SysOtherAppEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysOtherAppPageDTO extends MyTenantPageDTO {

    @Schema(description = "第三方应用类型")
    private SysOtherAppEnum type;

    @Schema(description = "第三方应用名，备注：同一租户不能重复，不同租户可以重复")
    private String name;

    @Schema(description = "第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复")
    private String appId;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
