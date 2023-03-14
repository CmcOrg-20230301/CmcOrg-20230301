package com.cmcorg20230301.engine.be.wx.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxOpenIdVO extends WxBaseVO {

    @Schema(description = "用户唯一标识")
    private String openid;

    @Schema(description = "会话密钥")
    private String session_key;

    @Schema(description = "用户在开放平台的唯一标识符")
    private String unionid;

}
