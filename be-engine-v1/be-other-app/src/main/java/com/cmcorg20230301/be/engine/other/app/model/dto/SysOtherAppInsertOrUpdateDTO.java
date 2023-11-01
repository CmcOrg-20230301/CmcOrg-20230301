package com.cmcorg20230301.be.engine.other.app.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysOtherAppInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @NotNull
    @Schema(description = "第三方应用类型")
    private SysOtherAppTypeEnum type;

    @NotBlank
    @Schema(description = "第三方应用名")
    private String name;

    @NotBlank
    @Schema(description = "第三方应用的 appId，备注：同一个类型下，所有租户不能重复，原因：比如接收公众号消息时，就无法找到具体是哪一个租户")
    private String appId;

    @NotBlank
    @Schema(description = "第三方应用的 secret")
    private String secret;

    @Schema(description = "用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容")
    private String subscribeReplyContent;

    @Schema(description = "二维码，备注：不是二维码图片的地址，而是二维码解码之后的值")
    private String qrCode;

    @Schema(description = "第三方应用的 openId/微信号，例如：接收微信公众号消息时的 ToUserName")
    private String openId;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
