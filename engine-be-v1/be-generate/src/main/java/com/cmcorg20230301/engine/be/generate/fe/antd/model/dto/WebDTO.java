package com.cmcorg20230301.engine.be.generate.fe.antd.model.dto;

import com.cmcorg20230301.engine.be.generate.model.enums.PageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class WebDTO {

    @Schema(description = "所有页面集合")
    private List<PageDTO> pageList;

    @Schema(description = "页面类型，所有页面集合下标")
    private Map<PageTypeEnum, Set<Integer>> pageTypeMap;

}
