package com.cmcorg20230301.be.engine.model.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "滚动列表查询")
public class ScrollListDTO {

    @Schema(description = "主键 id，如果为 null，则根据 backwardFlag，来查询最大 id或者最小 id，注意：不会查询该 id的数据")
    private Long id;

    @Schema(description = "本次查询的长度，默认：20")
    private Integer pageSize;

    @Schema(description = "是否向后查询，默认：false 根据 id，往前查询 true 根据 id，往后查询")
    private Boolean backwardFlag;

}
