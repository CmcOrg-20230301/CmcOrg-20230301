package com.cmcorg20230301.engine.be.dict.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SysDictListByDictKeyDTO {

    @NotBlank
    @Schema(description = "字典 key")
    private String dictKey;

}
