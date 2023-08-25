package com.cmcorg20230301.be.engine.role.model.vo;

import com.cmcorg20230301.be.engine.security.model.entity.SysRoleDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRoleInfoByIdVO extends SysRoleDO {

    @Schema(description = "用户 idSet")
    private Set<Long> userIdSet;

    @Schema(description = "菜单 idSet")
    private Set<Long> menuIdSet;

}
