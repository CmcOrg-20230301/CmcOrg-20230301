package com.cmcorg20230301.be.engine.email.exception;

import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER(300011, "操作失败：邮箱不存在，请重新输入"), //
    EMAIL_NOT_REGISTERED(300021, "操作失败：邮箱未注册，请重新输入"), //

    ;

    private final int code;
    private final String msg;
}
