package com.cmcorg20230301.be.engine.pay.google.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPayGooglePayConsumeDTO extends NotNullId {

    @NotNull
    @Schema(description = "支付配置主键 id")
    private Long sysPayConfigurationId;

}
