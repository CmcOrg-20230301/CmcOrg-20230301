package com.cmcorg20230301.be.engine.user.exception;

import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    ACCOUNT_CANNOT_BE_EMPTY(300011, "操作失败：邮箱/登录名/手机号码/微信 不能都为空"), //

    ;

    private final int code;
    private final String msg;

}
