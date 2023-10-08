package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO extends BaseTenantInsertOrUpdateDTO {

    @NotNull
    @Min(0)
    @Schema(description = "提现金额")
    private BigDecimal withdrawMoney;

    @NotBlank
    @Schema(description = "卡号")
    private String bankCardNo;

    @NotBlank
    @Schema(description = "开户行")
    private String openBankName;

    @NotBlank
    @Schema(description = "支行")
    private String branchBankName;

    @NotBlank
    @Schema(description = "收款人姓名")
    private String payeeName;

}
