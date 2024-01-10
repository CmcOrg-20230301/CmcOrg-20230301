package com.cmcorg20230301.be.engine.im.session.model.dto;

import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionType;
import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysImSessionInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @Schema(description = "会话名")
    private String name;

    /**
     * {@link ISysImSessionType}
     */
    @NotNull
    @Schema(description = "会话类型：101 私聊 201 群聊 301 客服，备注：只有在新建时，该值才有效")
    private Integer type;

    @Schema(description = "备注")
    private String remark;

}
