package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChangeBigDecimalNumberDTO extends NotNullId {

    @NotNull
    @Schema(description = "需要改变的数值")
    private BigDecimal number;

}
