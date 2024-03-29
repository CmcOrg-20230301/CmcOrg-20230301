package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiLineTypeEnum implements ISysActivitiLineType {

    NORMAL(101), // 普通：判断：outputType和 outputValue

    FUNCTION(201), // 函数调用：判断：outputName

    ;

    private final int code;

}
