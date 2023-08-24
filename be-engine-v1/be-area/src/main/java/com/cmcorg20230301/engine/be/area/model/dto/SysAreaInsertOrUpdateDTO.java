package com.cmcorg20230301.engine.be.area.model.dto;

import com.cmcorg20230301.engine.be.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysAreaInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "父节点id（顶级则为0）")
    private Long parentId;

    @NotBlank
    @Schema(description = "区域名")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "部门 idSet")
    private Set<Long> deptIdSet;

}
