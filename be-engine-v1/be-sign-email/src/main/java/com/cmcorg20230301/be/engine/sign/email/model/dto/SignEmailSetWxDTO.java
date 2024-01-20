package com.cmcorg20230301.be.engine.sign.email.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SignEmailSetWxDTO {

    @Min(1)
    @NotNull
    @Schema(description = "二维码 id")
    private Long qrCodeId;


    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @Schema(description = "邮箱验证码")
    private String emailCode;

}
