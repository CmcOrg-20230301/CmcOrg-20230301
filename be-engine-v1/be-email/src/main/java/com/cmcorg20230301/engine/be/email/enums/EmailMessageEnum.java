package com.cmcorg20230301.engine.be.email.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮件消息枚举类
 */
@AllArgsConstructor
@Getter
public enum EmailMessageEnum {

    SIGN_UP("邮箱注册", "尊敬的用户您好，您本次注册的验证码是（10分钟内有效）：{}"), //
    UPDATE_PASSWORD("修改密码", "尊敬的用户您好，您本次修改密码的验证码是（10分钟内有效）：{}"), //
    UPDATE_EMAIL("修改邮箱", "尊敬的用户您好，您本次修改邮箱的验证码是（10分钟内有效）：{}"), //
    SIGN_DELETE("账号注销", "尊敬的用户您好，您账号注销的验证码是（10分钟内有效）：{}"), //
    FORGET_PASSWORD("忘记密码", "尊敬的用户您好，您本次忘记密码的验证码是（10分钟内有效）：{}"), //
    BIND_EMAIL("绑定邮箱", "尊敬的用户您好，您本次绑定邮箱的验证码是（10分钟内有效）：{}"), //

    ;

    private final String subject; // 主题
    private final String contentTemp; // 消息模板

}
