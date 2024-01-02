package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotNullIdAndNotEmptyStringSet extends NotNullId {

    @NotEmpty
    @Schema(description = "值 set")
    private Set<Long> valueSet;

}
