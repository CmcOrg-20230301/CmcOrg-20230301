package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysOpenBankNameEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SysUserBankCardInsertOrUpdateUserSelfDTO {

    @Min(0)
    @Schema(description = "租户主键 id，可以为 null")
    private Long tenantId;

    @NotBlank
    @Pattern(regexp = BaseRegexConstant.BANK_DEBIT_CARD)
    @Schema(description = "卡号")
    private String bankCardNo;

    @NotNull
    @Schema(description = "开户行")
    private SysOpenBankNameEnum openBankName;

    @NotBlank
    @Schema(description = "支行")
    private String branchBankName;

    @NotBlank
    @Schema(description = "收款人姓名")
    private String payeeName;

}
