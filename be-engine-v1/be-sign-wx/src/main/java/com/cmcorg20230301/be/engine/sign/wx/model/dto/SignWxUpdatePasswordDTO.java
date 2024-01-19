package com.cmcorg20230301.be.engine.sign.wx.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SignWxUpdatePasswordDTO extends NotNullId {

    @NotBlank
    @Schema(description = "前端加密之后的新密码")
    private String newPassword;

    @NotBlank
    @Schema(description = "前端加密之后的原始新密码")
    private String originNewPassword;

}
