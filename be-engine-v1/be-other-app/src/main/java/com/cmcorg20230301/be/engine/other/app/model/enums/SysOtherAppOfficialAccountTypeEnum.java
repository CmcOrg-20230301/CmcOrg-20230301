package com.cmcorg20230301.be.engine.other.app.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方应用，公众号类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum SysOtherAppOfficialAccountTypeEnum {

    WX_OFFICIAL_ACCOUNT(101), // 微信公众号

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
