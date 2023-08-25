package com.cmcorg20230301.be.engine.socket.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyPageDTO;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysSocketPageDTO extends MyPageDTO {

    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "协议")
    private String scheme;

    @Schema(description = "主机")
    private String host;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "socket类型")
    private SysSocketTypeEnum type;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
