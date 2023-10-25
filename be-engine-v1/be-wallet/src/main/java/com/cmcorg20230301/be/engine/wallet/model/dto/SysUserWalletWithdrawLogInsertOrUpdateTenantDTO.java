package com.cmcorg20230301.be.engine.wallet.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletWithdrawLogInsertOrUpdateTenantDTO extends SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO {

    @NotNull
    @Schema(description = "租户主键 id")
    private Long tenantId;

}