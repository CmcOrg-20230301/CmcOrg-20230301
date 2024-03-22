package com.cmcorg20230301.be.engine.model.model.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotBlankString {

    @NotBlank
    @Schema(description = "值")
    private String value;

}
