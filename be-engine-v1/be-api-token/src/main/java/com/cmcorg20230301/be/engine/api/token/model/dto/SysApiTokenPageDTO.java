package com.cmcorg20230301.be.engine.api.token.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysApiTokenPageDTO extends MyTenantPageDTO {

    @Schema(description = "名称")
    private String name;

}
