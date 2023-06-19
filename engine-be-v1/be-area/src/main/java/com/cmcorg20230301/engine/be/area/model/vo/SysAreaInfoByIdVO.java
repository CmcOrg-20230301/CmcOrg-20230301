package com.cmcorg20230301.engine.be.area.model.vo;

import com.cmcorg20230301.engine.be.area.model.entity.SysAreaDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysAreaInfoByIdVO extends SysAreaDO {

    @Schema(description = "部门 idSet")
    private Set<Long> deptIdSet;

}
