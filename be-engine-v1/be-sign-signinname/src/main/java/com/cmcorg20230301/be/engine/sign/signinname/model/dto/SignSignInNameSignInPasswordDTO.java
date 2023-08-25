package com.cmcorg20230301.be.engine.sign.signinname.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignSignInNameSignInPasswordDTO extends SignInNameNotBlankDTO {

    @NotBlank
    @Schema(description = "前端加密之后的密码")
    private String password;

}
