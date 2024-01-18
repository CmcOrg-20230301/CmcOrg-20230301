package com.cmcorg20230301.be.engine.model.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysQrCodeSceneBindBO {

    @Schema(description = "用户主键 id，备注：不一定有值，如果存在值，则表示：该二维码所属应用，已经绑定了用户，需要让用户决定，是否覆盖或者其他操作")
    private Long userId;

    @Schema(description = "租户主键 id，备注：一定有值")
    private Long tenantId;

    @Schema(description = "例如：微信 appId，备注：一定有值")
    private String appId;

    @Schema(description = "例如：微信 openId，备注：一定有值")
    private String openId;

}
