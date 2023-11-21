package com.cmcorg20230301.be.engine.other.app.wx.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxUserInfoVO extends WxBaseVO {

    @Schema(description = "用户的唯一标识")
    private String openid;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private Integer sex;

    @Schema(description = "用户个人资料填写的省份")
    private String province;

    @Schema(description = "普通用户个人资料填写的城市")
    private String city;

    @Schema(description = "国家，如中国为CN")
    private String country;

    @Schema(description = "用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。")
    private String headimgurl;

    @Schema(description = "用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）")
    private String privilege;

    @Schema(description = "只有在用户将公众号绑定到微信开放平台账号后，才会出现该字段。")
    private String unionid;

}
