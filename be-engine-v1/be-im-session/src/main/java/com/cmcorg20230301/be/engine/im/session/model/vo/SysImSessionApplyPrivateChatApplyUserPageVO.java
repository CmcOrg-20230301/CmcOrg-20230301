package com.cmcorg20230301.be.engine.im.session.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysImSessionApplyPrivateChatApplyUserPageVO {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像文件主键 id")
    private Long avatarFileId;

}
