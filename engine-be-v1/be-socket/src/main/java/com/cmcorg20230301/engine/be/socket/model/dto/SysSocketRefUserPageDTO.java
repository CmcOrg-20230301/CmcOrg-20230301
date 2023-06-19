package com.cmcorg20230301.engine.be.socket.model.dto;

import com.cmcorg20230301.engine.be.security.model.dto.MyPageDTO;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysSocketRefUserPageDTO extends MyPageDTO {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "socket主键 id")
    private Long socketId;

    @Schema(description = "冗余字段，昵称")
    private String nickname;

    @Schema(description = "协议")
    private String scheme;

    @Schema(description = "主机")
    private String host;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "socket类型")
    private SysSocketTypeEnum type;

    @Schema(description = "备注")
    private String remark;

}
