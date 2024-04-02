package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;

import lombok.Data;

@Data
public class SysActivitiLineBO {

    /**
     * 类型 {@link ISysActivitiLineType}
     */
    private Integer type;

    /**
     * 函数调用
     */
    private SysActivitiFunctionCallItemBO functionCall;

}
