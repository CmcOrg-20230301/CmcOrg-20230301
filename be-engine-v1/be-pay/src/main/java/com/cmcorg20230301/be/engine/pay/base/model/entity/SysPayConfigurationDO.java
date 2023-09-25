package com.cmcorg20230301.be.engine.pay.base.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_pay_configuration")
@Data
@Schema(description = "v20230301：主表：支付配置")
public class SysPayConfigurationDO extends BaseEntity {

    @Schema(description = "是否是默认支付方式，备注：只会有一个默认支付方式")
    private Boolean defaultFlag;

    @Schema(description = "支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌")
    private SysPayTypeEnum type;

    @Schema(description = "支付名（不可重复）")
    private String name;

    @Schema(description = "支付平台，网关地址，例如：https://openapi.alipay.com/gateway.do")
    private String serverUrl;

    @Schema(description = "支付平台，应用 id")
    private String appId;

    @Schema(description = "支付平台，私钥")
    private String privateKey;

    @Schema(description = "支付平台，公钥")
    private String platformPublicKey;

    @Schema(description = "支付平台，异步接收地址")
    private String notifyUrl;

    @Schema(description = "支付平台，商户号")
    private String merchantId;

    @Schema(description = "支付平台，商户证书序列号")
    private String merchantSerialNumber;

    @Schema(description = "支付平台，商户APIV3密钥")
    private String apiV3Key;

}
