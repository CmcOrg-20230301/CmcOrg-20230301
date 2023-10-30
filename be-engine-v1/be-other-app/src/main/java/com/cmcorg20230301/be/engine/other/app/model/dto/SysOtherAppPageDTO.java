package com.cmcorg20230301.be.engine.other.app.model.dto;

import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysOtherAppPageDTO extends MyTenantPageDTO {

    @Schema(description = "第三方应用类型")
    private SysOtherAppTypeEnum type;

    @Schema(description = "第三方应用名")
    private String name;

    @Schema(description = "第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复")
    private String appId;

    @Schema(description = "用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容")
    private String subscribeReplyContent;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
