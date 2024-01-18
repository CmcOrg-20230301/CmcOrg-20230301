package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysQrCodeSceneBindExistUserDTO extends NotNullId {

    @NotNull
    @Schema(description = "操作类型：101 （默认）取消 201 覆盖")
    private Integer type;

}
