package com.cmcorg20230301.engine.be.socket.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * socket 在线状态
 */
@AllArgsConstructor
@Getter
public enum SysSocketOnlineTypeEnum {

    ONLINE(101), // 在线

    HIDDEN(201), // 隐身

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
