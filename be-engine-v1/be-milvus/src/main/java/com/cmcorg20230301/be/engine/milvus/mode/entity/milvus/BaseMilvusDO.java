package com.cmcorg20230301.be.engine.milvus.mode.entity.milvus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BaseMilvusDO {

    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "租户 id")
    private Long tenantId;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "返回值")
    private String result;

    @Schema(description = "向量转换前的文字")
    private String vectorText;

    @Schema(description = "向量集合")
    private List<Float> vectorList;

}
