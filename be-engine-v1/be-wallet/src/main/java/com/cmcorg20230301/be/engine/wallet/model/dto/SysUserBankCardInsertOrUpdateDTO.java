package com.cmcorg20230301.be.engine.wallet.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserBankCardInsertOrUpdateDTO extends SysUserBankCardInsertOrUpdateUserSelfDTO {

    @NotNull
    @Schema(description = "用户主键 id")
    private Long id;

}
