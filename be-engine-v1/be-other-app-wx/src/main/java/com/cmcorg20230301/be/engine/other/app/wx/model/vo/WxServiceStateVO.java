package com.cmcorg20230301.be.engine.other.app.wx.model.vo;

import cn.hutool.core.annotation.Alias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WxServiceStateVO extends WxBaseVO {

    @Alias(value = "service_state")
    @Schema(description = "当前的会话状态：0 未处理 1 由智能助手接待 2 待接入池排队中 3 由人工接待 4 已结束/未开始")
    private Integer serviceState;

}
