package com.cmcorg20230301.engine.be.socket.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WebSocketMessageDTO<T> {

    @Schema(description = "路径")
    private String uri;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "响应代码，成功返回：200")
    private Integer code;

}
