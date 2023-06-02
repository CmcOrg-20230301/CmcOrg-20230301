package com.cmcorg20230301.engine.be.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典 dictKey
 */
@AllArgsConstructor
@Getter
public enum SysDictDictKeyEnum {

    ;

    @EnumValue
    @JsonValue
    private final String name;

}
