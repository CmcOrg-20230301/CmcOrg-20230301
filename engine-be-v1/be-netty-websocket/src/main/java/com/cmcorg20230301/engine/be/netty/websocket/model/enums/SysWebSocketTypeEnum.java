package com.cmcorg20230301.engine.be.netty.websocket.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * WebSocket 在线状态
 */
@AllArgsConstructor
@Getter
public enum SysWebSocketTypeEnum {
    ONLINE((byte)1, "在线"), //
    HIDDEN((byte)2, "隐身"), //
    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

    @Nullable
    public static SysWebSocketTypeEnum getByCode(Byte code) {
        if (code == null) {
            return null;
        }
        for (SysWebSocketTypeEnum item : SysWebSocketTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
