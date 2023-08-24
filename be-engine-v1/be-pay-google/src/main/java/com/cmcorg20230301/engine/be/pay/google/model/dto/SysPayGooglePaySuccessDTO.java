package com.cmcorg20230301.engine.be.pay.google.model.dto;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPayGooglePaySuccessDTO extends NotNullId {

    @Schema(description = "购买成功后 Purchase对象的 getPurchaseToken()")
    private String token;

}
