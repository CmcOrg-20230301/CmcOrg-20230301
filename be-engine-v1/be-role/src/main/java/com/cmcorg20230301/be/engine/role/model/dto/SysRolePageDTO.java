package com.cmcorg20230301.be.engine.role.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRolePageDTO extends MyPageDTO {

    @Schema(description = "角色名（不能重复）")
    private String name;

    @Schema(description = "是否是默认角色，备注：只会有一个默认角色")
    private Boolean defaultFlag;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
