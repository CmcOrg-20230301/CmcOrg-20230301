package com.cmcorg20230301.be.engine.param.model.dto;

import com.cmcorg20230301.be.engine.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysParamInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotBlank
    @Schema(description = "配置名，以 id为不变值进行使用，不要用此属性")
    private String name;

    @NotBlank
    @Schema(description = "值")
    private String value;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

}
