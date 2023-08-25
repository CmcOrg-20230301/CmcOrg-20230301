package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayDTO {

    @Schema(description = "用户主键 id，必填")
    private Long userId;

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

    @Schema(description = "用户 openId，可空")
    private String openId;

    @Schema(description = "app包名，必须是创建登录 api项目时，创建 android客户端 id使用包名，例如：谷歌支付")
    private String packageName;

    @Schema(description = "对应购买商品的商品 id，例如：谷歌支付")
    private String productId;

    @Schema(description = "购买成功后 Purchase对象的 getPurchaseToken()，例如：谷歌支付")
    private String token;

}
