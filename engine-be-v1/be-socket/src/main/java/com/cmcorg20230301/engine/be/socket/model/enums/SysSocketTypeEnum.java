package com.cmcorg20230301.engine.be.socket.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "socket类型")
public enum SysSocketTypeEnum {

    TCP_PROTOBUF(101), //

    WEB_SOCKET(201), //

    UDP(301), //

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
