package com.cmcorg20230301.be.engine.sign.helper.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方应用类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum SysOtherAppEnum {

    DICT(101), // 微信小程序

    DICT_ITEM(102), // 微信公众号

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
