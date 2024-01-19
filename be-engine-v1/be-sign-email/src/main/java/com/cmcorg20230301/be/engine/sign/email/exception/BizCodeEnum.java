package com.cmcorg20230301.be.engine.sign.email.exception;

import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER(300011, "操作失败：该登录名已被占用，请重新输入"), //

    ;

    private final int code;
    private final String msg;
}
