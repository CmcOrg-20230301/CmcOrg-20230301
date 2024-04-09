package com.cmcorg20230301.be.engine.flow.activiti.model.enums;

import com.cmcorg20230301.be.engine.flow.activiti.model.interfaces.ISysActivitiParamItemType;
import com.cmcorg20230301.be.engine.flow.activiti.util.SysActivitiUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SysActivitiParamItemTypeEnum implements ISysActivitiParamItemType {

    TEXT(101), // 文字（默认）

    IMAGE(201), // 图片：url

    FUNCTION_CALL_SIMPLE(301), // 函数调用

    FUNCTION_CALL_CUSTOM(401), // 函数调用-高级

    URL(501), // 网络链接

    ;

    private final int code;

    static {

        for (SysActivitiParamItemTypeEnum item : SysActivitiParamItemTypeEnum.values()) {

            SysActivitiUtil.PARAM_ITEM_TYPE_MAP.put(item.getCode(), item);

        }

    }

}
