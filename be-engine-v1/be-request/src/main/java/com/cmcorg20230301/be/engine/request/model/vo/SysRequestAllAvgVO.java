package com.cmcorg20230301.be.engine.request.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysRequestAllAvgVO {

    @Schema(description = "请求的总数")
    private Long count;

    @Schema(description = "请求的平均耗时（毫秒）")
    private Integer avgMs;

}
