package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO extends BaseInsertOrUpdateDTO {

    @NotNull
    @Min(0)
    @Schema(description = "提现金额")
    private BigDecimal withdrawMoney;

}
