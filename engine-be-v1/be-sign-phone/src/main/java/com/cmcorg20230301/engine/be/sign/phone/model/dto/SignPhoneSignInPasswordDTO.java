package com.cmcorg20230301.engine.be.sign.phone.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignPhoneSignInPasswordDTO extends PhoneNotBlankDTO {

    @NotBlank
    @Schema(description = "密码")
    private String password;

}
