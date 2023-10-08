package com.cmcorg20230301.be.engine.wallet.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletWithdrawLogPageDTO extends SysUserWalletWithdrawLogPageUserSelfDTO {

    @Schema(description = "用户主键 id")
    private Long userId;

}
