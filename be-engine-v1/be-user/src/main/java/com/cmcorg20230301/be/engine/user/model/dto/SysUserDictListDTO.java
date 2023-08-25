package com.cmcorg20230301.be.engine.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserDictListDTO {

    @Schema(description = "是否追加 admin账号")
    private Boolean addAdminFlag;

}
