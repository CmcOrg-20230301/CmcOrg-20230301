package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_param")
@Data
@Schema(description = "主表：系统参数")
public class SysParamDO extends BaseEntity {

    @Schema(description = "配置名，以 id为不变值进行使用，不要用此属性")
    private String name;

    @Schema(description = "值")
    private String value;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "该参数的 uuid，用于：同步租户参数等操作，备注：不允许修改")
    private String uuid;

    @Schema(description = "系统内置：是 强制同步给租户 否 不同步给租户")
    private Boolean systemFlag;

}
