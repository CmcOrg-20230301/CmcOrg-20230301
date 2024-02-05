package com.cmcorg20230301.be.engine.other.app.wx.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WxServicerListItemVO {

    @Schema(description = "接待人员的userid")
    private String userid;

    @Schema(description = "接待人员的接待状态。0:接待中,1:停止接待。")
    private Integer status;

}
