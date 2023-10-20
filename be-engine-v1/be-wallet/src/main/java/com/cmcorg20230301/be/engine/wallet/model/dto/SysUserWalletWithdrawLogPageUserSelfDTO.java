package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawStatusEnum;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysUserWalletWithdrawTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserWalletWithdrawLogPageUserSelfDTO extends MyTenantPageDTO {

    @Schema(description = "提现编号")
    private Long id;

    @Schema(description = "卡号")
    private String bankCardNo;

    @Schema(description = "开户行")
    private String openBankName;

    @Schema(description = "支行")
    private String branchBankName;

    @Schema(description = "收款人姓名")
    private String payeeName;

    @Schema(description = "提现状态")
    private SysUserWalletWithdrawStatusEnum withdrawStatus;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "起始时间：创建时间")
    private Date ctBeginTime;

    @Schema(description = "结束时间：创建时间")
    private Date ctEndTime;

    @Schema(description = "提现金额：开始值")
    private BigDecimal beginWithdrawMoney;

    @Schema(description = "提现金额：结束值")
    private BigDecimal endWithdrawMoney;

    @Schema(description = "提现类型")
    private SysUserWalletWithdrawTypeEnum type;

}
