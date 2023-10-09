package com.cmcorg20230301.be.engine.wallet.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO {

    @NotNull
    @Min(0)
    @Schema(description = "提现金额")
    private BigDecimal withdrawMoney;

}
