package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysQrCodeSceneBindVO {

    @Schema(description = "是否：已经扫码")
    private Boolean sceneFlag;

    @Schema(description = "如果已经存在用户，则下次操作，需要传递的 id，备注：因为存在，则需要让用户进行选择：覆盖或者取消绑定")
    private Long existUserOperateId;

}
