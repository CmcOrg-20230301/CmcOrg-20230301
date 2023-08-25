package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotEmptyIdSet {

    @NotEmpty
    @Schema(description = "主键 idSet")
    private Set<Long> idSet;

}
