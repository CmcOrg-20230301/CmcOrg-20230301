package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletWithdrawLogPageUserSelfDTO extends MyTenantPageDTO {

    @Schema(description = "银行")
    private String bankName;

    @Schema(description = "户名")
    private String accountName;

    @Schema(description = "卡号")
    private String bankCardNo;

    @Schema(description = "开户行")
    private String openBankName;

    @Schema(description = "提现状态")
    private SysUserWalletWithdrawStatusEnum withdrawStatus;

}
