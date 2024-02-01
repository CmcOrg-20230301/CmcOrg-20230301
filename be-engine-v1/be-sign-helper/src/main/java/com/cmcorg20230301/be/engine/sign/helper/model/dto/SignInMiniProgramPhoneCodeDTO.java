package com.cmcorg20230301.be.engine.sign.helper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignInMiniProgramPhoneCodeDTO extends UserSignBaseDTO {

    @NotBlank
    @Schema(description = "第三方应用 appId")
    private String appId;

    @NotBlank
    @Schema(description = "手机号码 code")
    private String phoneCode;

}
