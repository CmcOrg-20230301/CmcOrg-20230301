package com.cmcorg20230301.engine.be.model.model.constant;

/**
 * 正则表达式的常量类
 */
public interface BaseRegexConstant {

    String NON_NEGATIVE_INTEGER = "^\\d+$"; // 非负整数：>= 0

    String NON_ZERO_INTEGER = "^-?[1-9]\\d*$"; // 非零整数

    String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"; // 邮箱

    String CODE_6_REGEXP = "^[0-9]{6}$"; // 6位数的验证码：数字

    String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$"; // 密码限制：必须包含大小写字母和数字，可以使用特殊字符，长度8-20

    String NICK_NAME_REGEXP = "^[\\u4E00-\\u9FA5A-Za-z0-9_-]{2,20}$"; // 用户昵称限制：只能包含中文，数字，字母，下划线，横杠，长度2-20

    String SIGN_IN_NAME_REGEXP = NICK_NAME_REGEXP; // 登录名限制：只能包含中文，数字，字母，下划线，横杠，长度2-20

    String PHONE = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$"; // 手机号码

    String CHINESE_STR = "^[\\u4e00-\\u9fa5]{0,}$"; // 中文

    String ID_NUMBER = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"; // 身份证号

}
