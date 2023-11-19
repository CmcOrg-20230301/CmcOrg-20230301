package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.pay.base.model.dto.BuyDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletRechargeUserSelfDTO extends BuyDTO {

    @NotNull
    @Schema(description = "值")
    private BigDecimal value;

}
