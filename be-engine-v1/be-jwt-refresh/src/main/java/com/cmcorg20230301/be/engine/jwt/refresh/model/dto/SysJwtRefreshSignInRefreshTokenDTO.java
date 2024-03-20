package com.cmcorg20230301.be.engine.jwt.refresh.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysJwtRefreshSignInRefreshTokenDTO {

    @Schema(description = "refreshToken")
    private String refreshToken;

}
