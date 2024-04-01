package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiParamItemType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiParamItemTypeEnum implements ISysActivitiParamItemType {

    TEXT(101), // 文字

    IMAGE(201), // 图片：url

    FUNCTION_CALL(301), // 函数调用

    ;

    private final int code;

}
