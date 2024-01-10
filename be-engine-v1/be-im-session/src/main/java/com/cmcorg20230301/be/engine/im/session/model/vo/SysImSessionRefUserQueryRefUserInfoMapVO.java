package com.cmcorg20230301.be.engine.im.session.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysImSessionRefUserQueryRefUserInfoMapVO {

    @Schema(description = "我在会话的昵称")
    private String sessionNickname;

    @Schema(description = "我在会话的头像地址")
    private String sessionAvatarUrl;

}
