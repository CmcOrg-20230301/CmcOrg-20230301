package com.cmcorg20230301.engine.be.menu.model.vo;

import com.cmcorg20230301.engine.be.security.model.entity.SysMenuDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuInfoByIdVO extends SysMenuDO {

    @Schema(description = "角色 idSet")
    private Set<Long> roleIdSet;

}
