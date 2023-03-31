package com.cmcorg20230301.engine.be.dict.model.dto;

import com.cmcorg20230301.engine.be.dict.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.engine.be.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotBlank
    @Schema(description = "字典 key（不能重复），字典项要冗余这个 key，目的：方便操作")
    private String dictKey;

    @NotBlank
    @Schema(description = "字典/字典项 名")
    private String name;

    @NotNull
    @Schema(description = "字典类型：1 字典 2 字典项")
    private SysDictTypeEnum type;

    @Schema(description = "字典项 value（数字 123...）备注：字典为 -1")
    private Integer value;

    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
