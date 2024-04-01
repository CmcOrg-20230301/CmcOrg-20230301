package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiTaskCategory;

import lombok.Data;

@Data
public class SysActivitiTaskBO {

    /**
     * 类型 {@link ISysActivitiTaskCategory}
     */
    private Integer category;

    /**
     * 预设内容
     */
    private String preset;

}
