package com.cmcorg20230301.be.engine.im.session.model.dto;

import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionType;
import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysImSessionPageDTO extends MyTenantPageDTO {

    @Schema(description = "会话名")
    private String name;

    /**
     * {@link ISysImSessionType}
     */
    @Schema(description = "会话类型：101 私聊 201 群聊 301 客服")
    private Integer type;

}
