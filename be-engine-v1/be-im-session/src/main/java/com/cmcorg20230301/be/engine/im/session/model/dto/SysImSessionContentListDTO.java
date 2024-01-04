package com.cmcorg20230301.be.engine.im.session.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.ScrollListDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.validation.constraints.Min;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysImSessionContentListDTO extends ScrollListDTO {

    @Min(1)
    @NonNull
    @Schema(description = "会话主键 id")
    private Long sessionId;

}
