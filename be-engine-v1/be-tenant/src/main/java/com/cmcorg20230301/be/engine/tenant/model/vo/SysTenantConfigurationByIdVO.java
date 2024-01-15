package com.cmcorg20230301.be.engine.tenant.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysTenantConfigurationByIdVO {

    @Schema(description = "是否启用：用户名注册功能，默认启用")
    private Boolean signInNameSignUpEnable;

    @Schema(description = "是否启用：邮箱注册功能，默认启用")
    private Boolean emailSignUpEnable;

    @Schema(description = "是否启用：手机号码注册功能，默认启用")
    private Boolean phoneSignUpEnable;

    @Schema(description = "微信扫码注册 url，如果没有，则表示不支持，微信扫码注册")
    private String wxQrCodeSignUpUrl;

}
