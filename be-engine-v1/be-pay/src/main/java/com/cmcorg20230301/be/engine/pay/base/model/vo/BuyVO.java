package com.cmcorg20230301.be.engine.pay.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyVO {

    @Schema(description = "实际的支付方式")
    private Integer sysPayType;

    @Schema(description = "支付返回的参数")
    private String payReturnValue;

    @Schema(description = "本系统的支付订单号")
    private String outTradeNo;

    @Schema(description = "支付配置主键 id")
    private Long sysPayConfigurationId;

}
