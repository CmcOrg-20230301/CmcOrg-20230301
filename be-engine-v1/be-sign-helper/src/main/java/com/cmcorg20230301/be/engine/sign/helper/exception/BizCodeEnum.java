package com.cmcorg20230301.be.engine.sign.helper.exception;

import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    EMAIL_HAS_BEEN_REGISTERED(300011, "该邮箱已被占用"), //
    PHONE_HAS_BEEN_REGISTERED(300021, "该手机号码已被占用"), //
    TOO_MANY_PASSWORD_ERROR(300031, "密码错误次数过多，已被冻结，请点击【忘记密码了】，进行密码修改"), //
    NO_PASSWORD_SET(300041, "未设置密码，请点击【忘记密码了】，进行密码设置"), //
    ACCOUNT_OR_PASSWORD_NOT_VALID(300051, "账号或密码错误"), //
    USER_DOES_NOT_EXIST(300071, "操作失败：用户不存在"), //
    PASSWORD_NOT_VALID(300081, "操作失败：当前密码错误"), //
    THE_ACCOUNT_HAS_ALREADY_BEEN_REGISTERED(300091, "操作失败：该账号已被注册，请重试"), //
    PASSWORD_RESTRICTIONS(300101, "密码限制：必须包含大小写字母和数字，可以使用特殊字符，长度8-20"), //
    SIGN_IN_NAME_EXIST_PLEASE_RE_ENTER(300111, "操作失败：该登录名已被占用，请重新输入"), //

    ;

    private final int code;
    private final String msg;

}
