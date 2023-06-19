package com.cmcorg20230301.engine.be.area.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_area_ref_dept")
@Data
@Schema(description = "关联表：区域，部门")
public class SysAreaRefDeptDO {

    @TableId
    @Schema(description = "区域主键 id")
    private Long areaId;

    @Schema(description = "部门主键 id")
    private Long deptId;

}
