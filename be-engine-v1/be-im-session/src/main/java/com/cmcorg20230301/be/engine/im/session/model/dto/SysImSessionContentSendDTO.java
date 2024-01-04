package com.cmcorg20230301.be.engine.im.session.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SysImSessionContentSendDTO {

    @Min(1)
    @NonNull
    @Schema(description = "会话主键 id")
    private Long sessionId;

    @NotBlank
    @Schema(description = "发送的内容")
    private String content;

    @NotNull
    @Schema(description = "创建时间的时间戳")
    private Long createTs;

}
