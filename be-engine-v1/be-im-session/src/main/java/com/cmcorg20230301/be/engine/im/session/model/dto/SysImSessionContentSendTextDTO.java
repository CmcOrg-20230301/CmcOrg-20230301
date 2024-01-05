package com.cmcorg20230301.be.engine.im.session.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysImSessionContentSendTextDTO {

    @NotBlank
    @Schema(description = "发送的内容")
    private String content;

    @NotNull
    @Schema(description = "创建时间的时间戳")
    private Long createTs;

}
