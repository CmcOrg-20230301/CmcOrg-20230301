package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetQrCodeVO {

    @Schema(description = "二维码的 url地址")
    private String qrCodeUrl;

    @Schema(description = "二维码 id")
    private Long qrCodeId;

    @Schema(description = "二维码过期时间戳")
    private Long expireTs;

}
