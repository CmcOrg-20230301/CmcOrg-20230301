package com.cmcorg20230301.engine.be.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayDTO {

    @Schema(description = "商户订单号，商户网站订单系统中唯一订单号，必填")
    private String outTradeNo;

    @Schema(description = "付款金额，单位为元，必填")
    private BigDecimal totalAmount;

    @Schema(description = "订单名称，必填")
    private String subject;

    @Schema(description = "支付过期时间，必填")
    private Date timeExpire;

    @Schema(description = "商品描述，可空")
    private String body;

    @Schema(description = "用户的 openId，看情况可空")
    private String openId;

}
