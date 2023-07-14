package com.cmcorg20230301.engine.be.pay.base.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayRefTypeEnum;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityNoId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_pay")
@Data
@Schema(description = "主表：支付")
public class SysPayDO extends BaseEntityNoId {

    @TableId
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "支付方式")
    private SysPayTypeEnum payType;

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "订单名称")
    private String subject;

    @Schema(description = "商品描述")
    private String body;

    @Schema(description = "订单原始的钱")
    private BigDecimal originPrice;

    @Schema(description = "订单支付的钱")
    private BigDecimal payPrice;

    @Schema(description = "订单支付的钱的单位，例如：人民币 CNY")
    private String payCurrency;

    @Schema(description = "订单过期时间")
    private Date expireTime;

    @Schema(description = "用户 openId")
    private String openId;

    @Schema(description = "订单状态")
    private SysPayTradeStatusEnum status;

    @Schema(description = "订单号")
    private String tradeNo;

    @Schema(description = "支付返回的参数")
    private String payReturnValue;

    @Schema(description = "关联的类型")
    private SysPayRefTypeEnum refType;

    @Schema(description = "关联的 id")
    private Long refId;

}
