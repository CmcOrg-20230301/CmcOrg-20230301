package com.cmcorg20230301.be.engine.sign.helper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignInBrowserCodeDTO extends UserSignBaseDTO {

    @NotBlank
    @Schema(description = "第三方应用 appId")
    private String appId;

    @NotBlank
    @Schema(description = "第三方应用 code")
    private String code;

}
