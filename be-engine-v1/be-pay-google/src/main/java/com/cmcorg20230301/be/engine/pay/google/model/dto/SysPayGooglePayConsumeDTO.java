package com.cmcorg20230301.be.engine.pay.google.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPayGooglePayConsumeDTO extends NotNullId {

    @Schema(description = "租户主键 id")
    private Long tenantId;

    @Schema(description = "支付配置主键 id")
    private Long sysPayConfigurationId;

}
