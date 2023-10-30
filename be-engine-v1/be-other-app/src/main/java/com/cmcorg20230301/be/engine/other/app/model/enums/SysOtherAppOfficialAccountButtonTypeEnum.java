package com.cmcorg20230301.be.engine.other.app.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方应用，公众号按钮类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum SysOtherAppOfficialAccountButtonTypeEnum {

    CLICK(101, "click"), // 点击

    VIEW(201, "view"), // 链接

    ;

    @EnumValue
    @JsonValue
    private final int code;

    private final String name;

}
