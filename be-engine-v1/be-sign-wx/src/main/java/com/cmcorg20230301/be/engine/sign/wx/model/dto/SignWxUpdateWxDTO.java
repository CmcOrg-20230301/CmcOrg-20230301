package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SignWxUpdateWxDTO {

    @Min(1)
    @NotNull
    @Schema(description = "旧的二维码 id")
    private Long oldQrCodeId;

    @Min(1)
    @NotNull
    @Schema(description = "新的二维码 id")
    private Long newQrCodeId;

}
