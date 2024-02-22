package com.cmcorg20230301.be.engine.other.app.wx.model.vo;

import cn.hutool.core.annotation.Alias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxOpenIdVO extends WxBaseVO {

    @Schema(description = "用户唯一标识")
    private String openid;

    @Alias(value = "access_token")
    @Schema(description = "网页授权接口调用凭证，注意：此 access_token与基础支持的 access_token不同")
    private String accessToken;

    @Schema(description = "用户统一标识（针对一个微信开放平台账号下的应用，同一用户的 unionid 是唯一的），只有当scope为 snsapi_userinfo时返回")
    private String unionid;

    @Alias(value = "expires_in")
    @Schema(description = "access_token接口调用凭证超时时间，单位（秒）")
    private Long expiresIn;

    @Schema(description = "微信的 appId，备注，微信不会返回，这里由程序进行赋值")
    private String appId;

}
