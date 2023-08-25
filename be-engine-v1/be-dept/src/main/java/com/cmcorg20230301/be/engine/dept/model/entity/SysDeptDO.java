package com.cmcorg20230301.be.engine.dept.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dept")
@Data
@Schema(description = "主表：部门")
public class SysDeptDO extends BaseEntityTree<SysDeptDO> {

    @Schema(description = "部门名")
    private String name;

}
