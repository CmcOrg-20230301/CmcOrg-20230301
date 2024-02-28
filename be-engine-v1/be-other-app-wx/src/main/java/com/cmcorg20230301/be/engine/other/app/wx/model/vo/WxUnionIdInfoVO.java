package com.cmcorg20230301.be.engine.other.app.wx.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxUnionIdInfoVO extends WxBaseVO {

    @Schema(description = "用户唯一标识")
    private String openid;

    @Schema(description = "只有在用户将公众号绑定到微信开放平台账号后，才会出现该字段。")
    private String unionid;

}
