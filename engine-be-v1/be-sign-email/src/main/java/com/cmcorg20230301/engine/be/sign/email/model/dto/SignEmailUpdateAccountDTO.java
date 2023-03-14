package com.cmcorg20230301.engine.be.sign.email.model.dto;

import com.cmcorg20230301.engine.be.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignEmailUpdateAccountDTO {

    @Size(max = 200)
    @NotBlank
    @Pattern(regexp = BaseRegexConstant.EMAIL)
    @Schema(description = "新邮箱")
    private String newEmail;

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @Schema(description = "旧邮箱验证码")
    private String oldEmailCode;

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @Schema(description = "新邮箱验证码")
    private String newEmailCode;

}
