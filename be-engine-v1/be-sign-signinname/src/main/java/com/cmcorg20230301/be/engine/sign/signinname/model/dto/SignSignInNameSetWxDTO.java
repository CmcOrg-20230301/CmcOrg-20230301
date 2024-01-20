package com.cmcorg20230301.be.engine.sign.signinname.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignSignInNameSetWxDTO extends NotNullId {

    @NotBlank
    @Schema(description = "前端加密之后的密码")
    private String currentPassword;

}
