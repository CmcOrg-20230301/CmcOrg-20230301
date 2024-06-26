package com.cmcorg20230301.be.engine.role.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @NotBlank
    @Schema(description = "角色名，不能重复")
    private String name;

    @Schema(description = "菜单 idSet")
    private Set<Long> menuIdSet;

    @Schema(description = "用户 idSet")
    private Set<Long> userIdSet;

    @Schema(description = "是否是默认角色，备注：只会有一个默认角色")
    private Boolean defaultFlag;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
