package com.cmcorg20230301.engine.be.dict.model.dto;

import com.cmcorg20230301.engine.be.security.model.dto.MyPageDTO;
import com.cmcorg20230301.engine.be.security.model.enums.SysDictTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictPageDTO extends MyPageDTO {

    @Schema(description = "字典 key（不能重复），字典项要冗余这个 key，目的：方便操作")
    private String dictKey;

    @Schema(description = "字典/字典项 名")
    private String name;

    @Schema(description = "字典类型")
    private SysDictTypeEnum type;

    @Schema(description = "描述/备注")
    private String remark;

    @Schema(description = "启用/禁用")
    private Boolean enableFlag;

}
