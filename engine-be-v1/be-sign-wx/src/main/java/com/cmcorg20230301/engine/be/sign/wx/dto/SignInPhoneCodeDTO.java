package com.cmcorg20230301.engine.be.sign.wx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignInPhoneCodeDTO {

    @NotBlank
    @Schema(description = "手机号码 code")
    private String phoneCode;

}
