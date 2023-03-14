package com.cmcorg20230301.engine.be.generate.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "表单输入类型")
public enum FormInputTypeEnum {

    TEXT, // 普通输入框
    SELECT, // 下拉框
    TREE_SELECT, // 树形下拉框

    ;

}
