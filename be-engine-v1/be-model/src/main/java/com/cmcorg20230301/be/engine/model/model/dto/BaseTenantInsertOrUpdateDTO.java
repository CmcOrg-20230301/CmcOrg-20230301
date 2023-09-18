package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseTenantInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @Schema(description = "租户 id，可以为空，为空则表示：默认租户：0")
    private Long tenantId;

}
