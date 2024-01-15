package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInVO {

    @Schema(description = "jwt")
    private String jwt;

    @Schema(description = "jwt过期时间戳")
    private Long jwtExpireTs;

    @Schema(description = "租户主键 id")
    private Long tenantId;

}
