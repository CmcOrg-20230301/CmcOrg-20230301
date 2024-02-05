package com.cmcorg20230301.be.engine.other.app.wx.model.vo;

import cn.hutool.core.annotation.Alias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxServicerListVO extends WxBaseVO {

    @Alias(value = "servicer_list")
    @Schema(description = "客服账号的接待人员列表")
    private List<WxServicerListItemVO> servicerList;

}
