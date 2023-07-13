package com.cmcorg20230301.engine.be.pay.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关联的类型枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayRefTypeEnum {

    NONE(101), // 无

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
