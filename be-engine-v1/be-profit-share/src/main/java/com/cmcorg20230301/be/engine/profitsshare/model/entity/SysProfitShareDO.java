package com.cmcorg20230301.be.engine.profitsshare.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.profitsshare.model.enums.SysProfitShareTypeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_profit_share")
@Data
@Schema(description = "主表：分账")
public class SysProfitShareDO extends BaseEntity {

    @Schema(description = "订单主键 id")
    private Long orderId;

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "分账的名称")
    private String name;

    @Schema(description = "用户支付的钱")
    private BigDecimal payMoney;

    @Schema(description = "第三方分账平台类型")
    private SysProfitShareTypeEnum type;

    @Schema(description = "第三方分账订单号，备注：是分账的那笔订单号，不是支付时的订单号，该值的作用：如果该值为空，则表示分账失败，如果不为空，则表示分账成功")
    private String otherProfitShareTransactionId;

    @Schema(description = "第三方应用的 appId")
    private String otherAppId;

    @Schema(description = "上级用户主键 id")
    private Long upUserId;

    @Schema(description = "冗余字段：上级用户第三方应用的 openId")
    private String upOtherAppOpenId;

    @Schema(description = "上级用户分成的钱，备注：0表示不用分账")
    private BigDecimal upMoney;

    @Schema(description = "上上级用户主键 id")
    private Long upUpUserId;

    @Schema(description = "冗余字段：上上级用户第三方应用的 openId")
    private String upUpOtherAppOpenId;

    @Schema(description = "上上级用户分成的钱，备注：0表示不用分账")
    private BigDecimal upUpMoney;

}
