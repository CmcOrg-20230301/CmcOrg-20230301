package com.cmcorg20230301.engine.be.socket.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * webSocket 在线状态
 */
@AllArgsConstructor
@Getter
public enum SysSocketOnlineTypeEnum {

    ONLINE(1), // 在线

    HIDDEN(2), // 隐身

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
