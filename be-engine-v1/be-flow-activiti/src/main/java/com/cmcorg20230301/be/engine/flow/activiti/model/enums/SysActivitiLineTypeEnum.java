package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiLineTypeEnum implements ISysActivitiLineType {

    NORMAL(101), // 普通判断（默认）

    FUNCTION_CALL(201), // 函数调用判断

    ;

    private final int code;

}
