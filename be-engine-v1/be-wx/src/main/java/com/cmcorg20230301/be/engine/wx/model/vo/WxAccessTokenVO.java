package com.cmcorg20230301.be.engine.wx.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxAccessTokenVO extends WxBaseVO {

    @Schema(description = "获取到的凭证")
    private String access_token;

    @Schema(description = "凭证有效时间，单位：秒。目前是7200秒之内的值。")
    private Integer expires_in;

}
