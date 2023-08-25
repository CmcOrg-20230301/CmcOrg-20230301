package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChangeNumberDTO extends NotEmptyIdSet {

    @NotNull
    @Schema(description = "需要改变的数值")
    private Long number;

}
