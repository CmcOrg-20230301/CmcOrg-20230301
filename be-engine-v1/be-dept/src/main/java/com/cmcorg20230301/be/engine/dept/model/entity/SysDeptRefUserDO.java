package com.cmcorg20230301.be.engine.dept.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_dept_ref_user")
@Data
@Schema(description = "关联表：部门，用户")
public class SysDeptRefUserDO {

    @Schema(description = "租户id")
    private Long tenantId;

    @TableId(type = IdType.INPUT)
    @Schema(description = "部门主键 id")
    private Long deptId;

    @Schema(description = "用户主键 id")
    private Long userId;

}
