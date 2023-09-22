package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictIntegerVO {

    @Schema(description = "传值用")
    private Integer id;

    @Schema(description = "显示用")
    private String name;

}
