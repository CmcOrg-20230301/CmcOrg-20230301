package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_area")
@Data
@Schema(description = "主表：区域")
public class SysAreaDO extends BaseEntityTree<SysAreaDO> {

    @Schema(description = "区域名")
    private String name;

}
