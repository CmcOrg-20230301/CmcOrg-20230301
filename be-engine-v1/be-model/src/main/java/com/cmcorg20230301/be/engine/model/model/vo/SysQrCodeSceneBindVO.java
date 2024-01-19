package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysQrCodeSceneBindVO {

    @Schema(description = "是否：已经扫码")
    private Boolean sceneFlag;

    @Schema(description = "错误信息")
    private String errorMsg;

}
