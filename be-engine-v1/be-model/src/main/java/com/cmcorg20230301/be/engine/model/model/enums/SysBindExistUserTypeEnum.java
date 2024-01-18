package com.cmcorg20230301.be.engine.model.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 绑定账户时，如果目标账户已经注册过时，需要进行的操作
 */
@Getter
@AllArgsConstructor
public enum SysBindExistUserTypeEnum {

    CANCEL(101), // 取消

    COVER(201), // 覆盖

    ;

    @JsonValue
    private final int code;

}
