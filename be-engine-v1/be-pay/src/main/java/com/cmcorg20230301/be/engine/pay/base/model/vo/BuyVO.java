package com.cmcorg20230301.be.engine.pay.base.model.vo;

import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyVO {

    @Schema(description = "实际的支付方式")
    private SysPayTypeEnum sysPayTypeEnum;

    @Schema(description = "支付返回的参数")
    private String payReturnValue;

}
