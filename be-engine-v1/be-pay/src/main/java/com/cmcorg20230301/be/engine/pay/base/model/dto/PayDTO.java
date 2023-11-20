package com.cmcorg20230301.be.engine.pay.base.model.dto;

import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Consumer;

@Data
public class PayDTO {

    @Schema(description = "是否使用：上级租户的支付方式")
    private Boolean useParentTenantPayFlag;

    /**
     * {@link ISysPayType}
     */
    @Schema(description = "支付方式，必填")
    private Integer payType;

    @Schema(description = "租户 id，备注：只用于获取支付参数配置，无其他用处，必填")
    private Long tenantId;

    @Schema(description = "用户主键 id，必填")
    private Long userId;

    @Schema(description = "付款金额，单位为元，必填")
    private BigDecimal totalAmount;

    @Schema(description = "订单名称，必填")
    private String subject;

    @Schema(description = "支付过期时间，一般为 30分钟，必填")
    private Date expireTime;

    @Schema(description = "商品描述，可空")
    private String body;

    @Schema(description = "商户订单号，商户网站订单系统中唯一订单号，不用传递，会在调用支付前，自动赋值")
    private String outTradeNo;

    @Schema(description = "用户 openId，可空")
    private String openId;

    @Schema(description = "app包名，必须是创建登录 api项目时，创建 android客户端 id使用包名，例如：谷歌支付")
    private String packageName;

    @Schema(description = "对应购买商品的商品 id，例如：谷歌支付")
    private String productId;

    @Schema(description = "购买成功后 Purchase对象的 getPurchaseToken()，例如：谷歌支付")
    private String token;

    @Schema(description = "支付配置，不必传，如果传递此字段，那么配置会从该字段里面取值，备注：调用支付之后，这个字段会被赋值")
    private SysPayConfigurationDO sysPayConfigurationDoTemp;

    @Schema(description = "在设置 sysPayConfigurationDoTemp对象时进行检查，可以为 null")
    private Consumer<SysPayConfigurationDO> checkSysPayConfigurationDoConsumer;

    @Schema(description = "备注")
    private String remark;

    /**
     * 备注：需要每个支付，在调用支付前，先调用本方法，目的：可以让调用方，在支付前，进行一些检查操作
     */
    public void setSysPayConfigurationDoTemp(SysPayConfigurationDO sysPayConfigurationDoTemp,
        boolean handleDefaultPayTypeFlag) {

        Consumer<SysPayConfigurationDO> checkSysPayConfigurationDoConsumer = getCheckSysPayConfigurationDoConsumer();

        // 不处理：设置默认支付方式
        if (checkSysPayConfigurationDoConsumer != null && !handleDefaultPayTypeFlag) {
            checkSysPayConfigurationDoConsumer.accept(sysPayConfigurationDoTemp); // 执行：检查
        }

        this.sysPayConfigurationDoTemp = sysPayConfigurationDoTemp;

    }

}
