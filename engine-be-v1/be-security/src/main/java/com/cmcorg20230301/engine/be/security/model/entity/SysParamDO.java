package com.cmcorg20230301.engine.be.security.model.entity;

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

}
