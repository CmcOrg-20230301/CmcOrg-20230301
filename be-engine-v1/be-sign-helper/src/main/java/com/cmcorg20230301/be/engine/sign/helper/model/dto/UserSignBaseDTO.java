package com.cmcorg20230301.be.engine.sign.helper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserSignBaseDTO {

    @Schema(description = "租户id，可以为空，为空则表示：默认租户：0")
    private Long tenantId;

}
