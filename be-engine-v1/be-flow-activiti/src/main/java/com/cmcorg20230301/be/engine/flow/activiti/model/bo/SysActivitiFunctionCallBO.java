package com.cmcorg20230301.be.engine.flow.activiti.model.bo;

import lombok.Data;

@Data
public class SysActivitiFunctionCallBO {

    /**
     * 类型：一般为：function
     */
    private String type;

    /**
     * 函数描述
     */
    private SysActivitiFunctionCallItemBO function;

}
