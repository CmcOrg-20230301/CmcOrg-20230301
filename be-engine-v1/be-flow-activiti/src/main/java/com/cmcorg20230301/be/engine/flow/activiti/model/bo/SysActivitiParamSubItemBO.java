package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiParamItemType;

import lombok.Data;

@Data
public class SysActivitiParamSubItemBO {

    /**
     * 类型 {@link ISysActivitiParamItemType}
     */
    private Integer type;

    /**
     * 值
     */
    private String value;

}
