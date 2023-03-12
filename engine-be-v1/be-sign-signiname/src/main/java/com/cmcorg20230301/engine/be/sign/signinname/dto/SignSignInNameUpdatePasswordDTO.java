package com.cmcorg20230301.engine.be.sign.signinname.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignSignInNameUpdatePasswordDTO {

    @NotBlank
    @Schema(description = "前端加密之后的旧密码")
    private String oldPassword;

    @NotBlank
    @Schema(description = "前端加密之后的新密码")
    private String newPassword;

    @NotBlank
    @Schema(description = "前端加密之后的原始新密码")
    private String originNewPassword;

}
