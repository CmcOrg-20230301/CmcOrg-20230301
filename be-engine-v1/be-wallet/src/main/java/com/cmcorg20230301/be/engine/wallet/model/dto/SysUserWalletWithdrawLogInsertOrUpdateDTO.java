package com.cmcorg20230301.be.engine.wallet.model.dto;

import org.jetbrains.annotations.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SysUserWalletWithdrawLogInsertOrUpdateDTO extends SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO {

    @NotNull
    @Schema(description = "用户主键 id")
    private Long userId;

}
