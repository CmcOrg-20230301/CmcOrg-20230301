package com.cmcorg20230301.be.engine.im.session.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SysImSessionContentSendTextListDTO {

    @Min(1)
    @NonNull
    @Schema(description = "会话主键 id")
    private Long sessionId;

    @NotEmpty
    @Schema(description = "发送内容集合")
    private List<SysImSessionContentSendTextDTO> contentList;

}
