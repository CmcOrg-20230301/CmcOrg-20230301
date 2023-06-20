package com.cmcorg20230301.engine.be.post.model.dto;

import com.cmcorg20230301.engine.be.security.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPostPageDTO extends MyPageDTO {

    @Schema(description = "岗位名")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

}
