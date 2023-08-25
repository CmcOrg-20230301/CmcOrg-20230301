package com.cmcorg20230301.be.engine.wx.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxPhoneByCodeVO extends WxBaseVO {

    @Schema(description = "用户手机号信息")
    private WxPhoneInfoVO phone_info;

    @Data
    public static class WxPhoneInfoVO {

        @Schema(description = "用户绑定的手机号（国外手机号会有区号）")
        private String phoneNumber;

        @Schema(description = "没有区号的手机号")
        private String purePhoneNumber;

        @Schema(description = "区号")
        private String countryCode;

    }

}
