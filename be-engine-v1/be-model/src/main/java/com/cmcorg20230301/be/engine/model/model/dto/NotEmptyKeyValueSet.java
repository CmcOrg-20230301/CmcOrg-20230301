package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotEmptyKeyValueSet {

    @NotBlank
    @Schema(description = "key")
    private String key;

    @NotEmpty
    @Schema(description = "值 set")
    private Set<KeyValue> keyValueSet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyValue {

        private String key;

        private Object value;

    }

}
