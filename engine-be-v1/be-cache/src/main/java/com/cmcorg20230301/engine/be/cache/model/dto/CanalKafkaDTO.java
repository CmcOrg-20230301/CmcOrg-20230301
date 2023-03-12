package com.cmcorg20230301.engine.be.cache.model.dto;

import com.cmcorg20230301.engine.be.cache.model.enums.CanalKafkaTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CanalKafkaDTO {

    @Schema(description = "数据库名称")
    private String database;

    @Schema(description = "表名称，备注：可能为空")
    private String table;

    @Schema(description = "类型")
    private CanalKafkaTypeEnum type;

}
