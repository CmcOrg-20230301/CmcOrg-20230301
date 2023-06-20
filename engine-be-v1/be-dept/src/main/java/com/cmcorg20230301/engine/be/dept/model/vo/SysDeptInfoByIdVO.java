package com.cmcorg20230301.engine.be.dept.model.vo;

import com.cmcorg20230301.engine.be.dept.model.entity.SysDeptDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptInfoByIdVO extends SysDeptDO {

    @Schema(description = "区域 idSet")
    private Set<Long> areaIdSet;

    @Schema(description = "用户 idSet")
    private Set<Long> userIdSet;

}
