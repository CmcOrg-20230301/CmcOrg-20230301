package com.cmcorg20230301.be.engine.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典类型
 */
@AllArgsConstructor
@Getter
public enum SysDictTypeEnum {

    DICT(1), // 字典
    DICT_ITEM(2), // 字典项

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
