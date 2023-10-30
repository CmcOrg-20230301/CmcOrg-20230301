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
    @Schema(description = "第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复")
    private String appId;

    @NotBlank
    @Schema(description = "第三方应用的 secret")
    private String secret;

    @Schema(description = "用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容")
    private String subscribeReplyContent;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
