package com.cmcorg20230301.be.engine.file.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysFilePageDTO extends SysFilePageSelfDTO {

    @Schema(description = "归属者用户主键 id（拥有全部权限）")
    private Long belongId;

}
