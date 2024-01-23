package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SignWxSetSingleSignInDTO {

    @Min(1)
    @NotNull
    @Schema(description = "当前微信的二维码 id")
    private Long currentQrCodeId;

    @Min(1)
    @NotNull
    @Schema(description = "统一登录微信的二维码 id")
    private Long singleSignInQrCodeId;

}
