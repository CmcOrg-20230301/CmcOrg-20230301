package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignInMiniProgramCodeDTO {

    @NotBlank
    @Schema(description = "微信 code")
    private String code;

}
