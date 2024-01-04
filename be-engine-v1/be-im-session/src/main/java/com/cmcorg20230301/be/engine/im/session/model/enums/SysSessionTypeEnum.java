package com.cmcorg20230301.be.engine.im.session.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysSessionTypeEnum implements ISysImSessionType {

    PRIVATE_CHAT(101), // 私聊

    GROUP_CHAT(201), // 群聊

    CUSTOMER(301), // 客服

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
