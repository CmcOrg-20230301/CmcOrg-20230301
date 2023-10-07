package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletPageDTO extends MyTenantPageDTO {

    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

}
