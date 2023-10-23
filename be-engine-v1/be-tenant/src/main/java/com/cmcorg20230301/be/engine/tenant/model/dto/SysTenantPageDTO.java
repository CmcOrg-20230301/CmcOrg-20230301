package com.cmcorg20230301.be.engine.tenant.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantPageDTO extends MyTenantPageDTO {

    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "租户名")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "是否独立支付，备注：例如用户在充值钱包的钱时，如果为否，则会扣除租户的钱包余额，如果为是，则不会扣除租户的钱包余额")
    private Boolean independentPayFlag;

}
