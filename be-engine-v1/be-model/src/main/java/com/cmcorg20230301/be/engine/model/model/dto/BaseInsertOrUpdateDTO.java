package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseInsertOrUpdateDTO {

    @Min(1)
    @Schema(description = "主键 id")
    private Long id;

}
