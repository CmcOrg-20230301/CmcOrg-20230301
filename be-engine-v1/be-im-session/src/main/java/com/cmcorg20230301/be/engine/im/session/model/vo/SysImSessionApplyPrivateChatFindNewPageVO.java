package com.cmcorg20230301.be.engine.im.session.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysImSessionApplyPrivateChatFindNewPageVO {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像地址")
    private String avatarUrl;

}