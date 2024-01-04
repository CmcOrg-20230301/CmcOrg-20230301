package com.cmcorg20230301.be.engine.im.session.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SysImSessionContentSendListDTO {

    @NotEmpty
    @Schema(description = "发送内容集合")
    private List<SysImSessionContentSendDTO> dtoList;

}
