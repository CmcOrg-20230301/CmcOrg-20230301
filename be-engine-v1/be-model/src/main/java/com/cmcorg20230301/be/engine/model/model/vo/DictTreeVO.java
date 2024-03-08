package com.cmcorg20230301.be.engine.model.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DictTreeVO extends DictVO {

    @Schema(description = "父级 id")
    private Long parentId;

    public DictTreeVO() {}

    public DictTreeVO(Long id, String name, Long parentId) {
        super(id, name);
        this.parentId = parentId;
    }

}
