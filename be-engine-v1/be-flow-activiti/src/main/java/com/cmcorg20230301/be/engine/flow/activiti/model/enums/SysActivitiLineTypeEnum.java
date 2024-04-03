package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiLineTypeEnum implements ISysActivitiLineType {

    NORMAL(101, false), // 普通判断（默认）

    FUNCTION_CALL_SIMPLE(201, true), // 函数调用判断

    FUNCTION_CALL_CUSTOM(301, true), // 函数调用判断-高级

    ;

    private final int code;
    private final Boolean functionCallFlag;

}
