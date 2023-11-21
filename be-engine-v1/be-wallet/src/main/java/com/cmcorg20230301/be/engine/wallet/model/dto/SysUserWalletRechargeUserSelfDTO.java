package com.cmcorg20230301.be.engine.wallet.model.dto;

import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SysUserWalletRechargeUserSelfDTO {

    /**
     * {@link ISysPayType}
     */
    @Schema(description = "支付方式，备注：如果为 null，则表示用默认支付方式")
    private Integer sysPayType;

    @NotNull
    @Schema(description = "值")
    private BigDecimal value;

}
