package com.cmcorg20230301.be.engine.pay.base.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPayConfigurationInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @NotNull
    @Schema(description = "支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌")
    private SysPayTypeEnum type;

    @NotBlank
    @Schema(description = "支付名（不可重复）")
    private String name;

    @NotBlank
    @Schema(description = "支付平台，网关地址，例如：https://openapi.alipay.com/gateway.do")
    private String serverUrl;

    @NotBlank
    @Schema(description = "支付平台，应用 id")
    private String appId;

    @NotBlank
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

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
