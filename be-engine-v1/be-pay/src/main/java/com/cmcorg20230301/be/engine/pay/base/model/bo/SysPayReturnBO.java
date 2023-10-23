package com.cmcorg20230301.be.engine.pay.base.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPayReturnBO {

    @Schema(description = "调用支付之后，支付平台返回的数据，用于传递参数和检查问题")
    private String payReturnValue;

    @Schema(description = "支付平台，应用 id")
    private String payAppId;

}
