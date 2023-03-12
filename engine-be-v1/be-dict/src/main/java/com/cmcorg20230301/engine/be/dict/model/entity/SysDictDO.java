package com.cmcorg20230301.engine.be.dict.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.dict.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict")
@Data
@Schema(description = "主表：字典")
public class SysDictDO extends BaseEntity {

    @Schema(description = "字典 key（不能重复），字典项要冗余这个 key，目的：方便操作")
    private String dictKey;

    @Schema(description = "字典/字典项 名")
    private String name;

    @Schema(description = "字典类型：1 字典 2 字典项")
    private SysDictTypeEnum type;

    @Schema(description = "字典项 value（数字 123...）备注：字典为 -1")
    private Byte value;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

}
