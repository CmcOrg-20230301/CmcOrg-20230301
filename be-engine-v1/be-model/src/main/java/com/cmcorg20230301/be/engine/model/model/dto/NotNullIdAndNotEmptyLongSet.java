package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotNullIdAndNotEmptyLongSet extends NotNullId {

    public NotNullIdAndNotEmptyLongSet(Long id, Set<Long> valueSet) {

        super(id);
        this.valueSet = valueSet;

    }

    @NotEmpty
    @Schema(description = "值 set")
    private Set<Long> valueSet;

}
