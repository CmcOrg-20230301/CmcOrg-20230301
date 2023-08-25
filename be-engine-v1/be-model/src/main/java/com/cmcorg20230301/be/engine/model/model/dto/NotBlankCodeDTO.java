package com.cmcorg20230301.be.engine.model.model.dto;

import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotBlankCodeDTO {

    @Pattern(regexp = BaseRegexConstant.CODE_6_REGEXP)
    @NotBlank
    @Schema(description = "验证码")
    private String code;

}
