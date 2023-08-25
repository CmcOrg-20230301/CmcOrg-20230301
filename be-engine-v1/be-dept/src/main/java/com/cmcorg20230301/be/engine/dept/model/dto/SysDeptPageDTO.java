package com.cmcorg20230301.be.engine.dept.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptPageDTO extends MyPageDTO {

    @Schema(description = "部门名")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

}
