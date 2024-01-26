package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignWxSetSingleSignInPhoneSendCodeDTO {

    @Size(max = 100)
    @NotBlank
    @Pattern(regexp = BaseRegexConstant.PHONE)
    @Schema(description = "统一登录的手机号码")
    private String singleSignInPhone;

}
