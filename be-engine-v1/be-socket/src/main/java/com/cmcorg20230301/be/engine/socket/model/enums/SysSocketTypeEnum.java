package com.cmcorg20230301.be.engine.socket.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * socket类型
 */
@AllArgsConstructor
@Getter
public enum SysSocketTypeEnum {

    TCP_PROTOBUF(101), //

    WEB_SOCKET(201), //

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
