package com.cmcorg20230301.be.engine.menu.exception;

import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    MENU_URI_IS_EXIST(300011, "操作失败：path 重复"), //

    ;

    private final int code;
    private final String msg;
}
