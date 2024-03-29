package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import lombok.Data;

@Data
public class SysActivitiFunctionCallBO {

    /**
     * 函数名
     */
    private String name;

    /**
     * 函数描述
     */
    private String description;

    /**
     * 函数入参
     */
    private SysActivitiFunctionCallParametersBO parameters;

}
