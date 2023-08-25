package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LongObjectMapVO<T> {

    @Schema(description = "map对象")
    private Map<Long, T> map;

}
