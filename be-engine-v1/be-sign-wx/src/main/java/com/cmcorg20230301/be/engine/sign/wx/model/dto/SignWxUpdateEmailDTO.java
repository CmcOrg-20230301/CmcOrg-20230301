package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignWxUpdateEmailDTO extends NotNullId {

    @Size(max = 200)
    @NotBlank
    @Pattern(regexp = BaseRegexConstant.EMAIL)
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @Schema(description = "邮箱验证码")
    private String code;

}
