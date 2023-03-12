package com.cmcorg20230301.engine.be.dict.model.vo;

import com.cmcorg20230301.engine.be.dict.model.entity.SysDictDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDictTreeVO extends SysDictDO {

    @Schema(description = "字典的子节点")
    private List<SysDictTreeVO> children;

}
