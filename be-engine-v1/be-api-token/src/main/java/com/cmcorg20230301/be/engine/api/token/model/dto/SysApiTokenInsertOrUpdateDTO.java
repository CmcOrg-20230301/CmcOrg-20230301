package com.cmcorg20230301.be.engine.api.token.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysApiTokenInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @NotBlank
    @Schema(description = "名称")
    private String name;

}