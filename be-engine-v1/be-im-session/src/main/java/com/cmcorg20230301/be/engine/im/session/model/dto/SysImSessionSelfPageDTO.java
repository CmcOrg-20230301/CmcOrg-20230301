package com.cmcorg20230301.be.engine.im.session.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysImSessionSelfPageDTO extends MyTenantPageDTO {

    @Schema(description = "用户主键 id，备注：由程序进行赋值", hidden = true)
    private Long userId;

    @Schema(description = "私聊关联的另外一个用户主键 id")
    private Long privateChatRefUserId;

}
