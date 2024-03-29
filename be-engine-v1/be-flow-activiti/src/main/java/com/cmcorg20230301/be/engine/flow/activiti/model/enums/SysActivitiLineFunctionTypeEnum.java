package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiLineFunctionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiLineFunctionTypeEnum implements ISysActivitiLineFunctionType {

    NORMAL(101), // 普通

    CUSTOM(201), // 自定义

    ;

    private final int code;

}
