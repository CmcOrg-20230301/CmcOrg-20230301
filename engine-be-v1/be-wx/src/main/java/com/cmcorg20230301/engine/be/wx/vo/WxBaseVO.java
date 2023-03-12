package com.cmcorg20230301.engine.be.wx.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WxBaseVO {

    @Schema(description = "错误码：0 请求成功")
    private Integer errcode;

    @Schema(description = "错误信息")
    private String errmsg;

}
