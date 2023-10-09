package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserBankCardInsertOrUpdateUserSelfDTO extends BaseTenantInsertOrUpdateDTO {

    @NotBlank
    @Pattern(regexp = BaseRegexConstant.BANK_DEBIT_CARD)
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
