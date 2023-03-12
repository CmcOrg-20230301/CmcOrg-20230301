package com.cmcorg20230301.engine.be.sign.signinname.dto;

import com.cmcorg20230301.engine.be.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignSignInNameUpdateAccountDTO {

    @Size(max = 20)
    @NotBlank
    @Pattern(regexp = BaseRegexConstant.SIGN_IN_NAME_REGEXP)
    @Schema(description = "新登录名")
    private String newSignInName;

    @NotBlank
    @Schema(description = "前端加密之后的密码")
    private String currentPassword;

}
