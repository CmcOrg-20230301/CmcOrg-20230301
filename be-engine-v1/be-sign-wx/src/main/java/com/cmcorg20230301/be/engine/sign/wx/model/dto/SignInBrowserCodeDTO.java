package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import com.cmcorg20230301.be.engine.sign.helper.model.dto.UserSignBaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignInBrowserCodeDTO extends UserSignBaseDTO {

    @NotBlank
    @Schema(description = "微信 code")
    private String code;

}
