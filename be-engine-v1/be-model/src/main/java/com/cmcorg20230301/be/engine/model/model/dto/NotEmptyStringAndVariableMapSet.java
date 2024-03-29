package com.cmcorg20230301.be.engine.model.model.dto;

import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotEmptyStringAndVariableMapSet {

    @NotEmpty
    @Schema(description = "主键 idSet")
    private Set<String> idSet;

    @Schema(description = "参数：map")
    private Map<String, Object> variableMap;

}
