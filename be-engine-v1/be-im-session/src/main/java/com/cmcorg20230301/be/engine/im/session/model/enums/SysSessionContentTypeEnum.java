package com.cmcorg20230301.be.engine.im.session.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话内容类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysSessionContentTypeEnum implements ISysImSessionType {

    TEXT(101), // 文字

    IMAGE(201), // 图片

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
