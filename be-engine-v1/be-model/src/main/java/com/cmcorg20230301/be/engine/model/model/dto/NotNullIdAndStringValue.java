package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotNullIdAndStringValue extends NotNullId {

    public NotNullIdAndStringValue(Long id, String value) {

        super(id);
        this.value = value;

    }

    @NotBlank
    @Schema(description = "值")
    private String value;

}
