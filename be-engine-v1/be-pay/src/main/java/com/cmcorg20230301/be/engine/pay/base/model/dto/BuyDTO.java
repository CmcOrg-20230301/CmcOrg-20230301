package com.cmcorg20230301.be.engine.pay.base.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BuyDTO extends NotNullId {

    /**
     * {@link ISysPayType}
     */
    @Schema(description = "支付方式，备注：如果为 null，则表示用默认支付方式")
    private Integer sysPayType;

}
