package com.cmcorg20230301.be.engine.sign.phone.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignPhoneSetPasswordDTO {

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @Schema(description = "手机验证码")
    private String code;

    @NotBlank
    @Schema(description = "前端加密之后的新密码")
    private String newPassword;

    @NotBlank
    @Schema(description = "前端加密之后的原始新密码")
    private String originNewPassword;

}
