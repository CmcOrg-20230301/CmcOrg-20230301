package com.cmcorg20230301.engine.be.dict.exception;

import com.cmcorg20230301.engine.be.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    SAME_KEY_OR_NAME_EXIST(300011, "操作失败：存在相同字典【key/名称】"), //
    SAME_VALUE_OR_NAME_EXIST(300021, "操作失败：存在相同字典项【value/名称】"), //
    VALUE_CANNOT_BE_EMPTY(300031, "操作失败：字典项【value】不能为空"), //

    ;

    private int code;
    private String msg;
}
