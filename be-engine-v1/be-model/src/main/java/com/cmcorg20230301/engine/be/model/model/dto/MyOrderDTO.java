package com.cmcorg20230301.engine.be.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MyOrderDTO {

    @Schema(description = "排序的字段名")
    private String name;

    @Schema(description = "ascend（升序，默认） descend（降序）")
    private String value;

}
