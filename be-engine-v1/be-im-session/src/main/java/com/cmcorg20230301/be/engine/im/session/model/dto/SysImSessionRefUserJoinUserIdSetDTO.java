package com.cmcorg20230301.be.engine.im.session.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysImSessionRefUserJoinUserIdSetDTO extends NotNullIdAndNotEmptyLongSet {

    @Schema(defaultValue = "是否是：私聊加入聊天")
    private Boolean privateChatFlag;

}
