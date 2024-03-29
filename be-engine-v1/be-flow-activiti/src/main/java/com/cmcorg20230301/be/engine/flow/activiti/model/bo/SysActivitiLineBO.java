package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineFunctionType;
import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;

import lombok.Data;

@Data
public class SysActivitiLineBO {

    /**
     * 类型 {@link ISysActivitiLineType}
     */
    private Integer type;

    /**
     * 类型 {@link ISysActivitiLineFunctionType}
     */
    private Integer functionType;

    /**
     * 函数描述
     */
    private String description;

}
