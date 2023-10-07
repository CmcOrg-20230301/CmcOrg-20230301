package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @NotNull
    @Min(value = 0)
    @Schema(description = "总金额")
    private BigDecimal totalMoney;

    @NotNull
    @Min(value = 0)
    @Schema(description = "可提现的钱")
    private BigDecimal withdrawableMoney;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

}
