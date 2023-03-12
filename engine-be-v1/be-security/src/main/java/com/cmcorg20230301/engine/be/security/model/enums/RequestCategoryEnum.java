package com.cmcorg20230301.engine.be.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Schema(description = "请求类别")
public enum RequestCategoryEnum {

    PC_BROWSER_WINDOWS(101, "电脑-windows-浏览器"), //
    PC_BROWSER_MAC(102, "电脑-mac-浏览器"), //
    PC_BROWSER_LINUX(103, "电脑-linux-浏览器"), //

    PC_CLIENT_WINDOWS(201, "电脑-windows-客户端"), //
    PC_CLIENT_MAC(202, "电脑-mac-客户端"), //
    PC_CLIENT_LINUX(203, "电脑-linux-客户端"), //

    ANDROID(301, "安卓端"), //
    ANDROID_BROWSER(302, "安卓-浏览器"), //

    IOS(401, "苹果端"), //
    IOS_BROWSER(402, "苹果-浏览器"), //

    MINI_PROGRAM_WE_CHAT_ANDROID(501, "小程序-微信-安卓"), //
    MINI_PROGRAM_WE_CHAT_IOS(502, "小程序-微信-苹果"), //

    ;

    @EnumValue
    @JsonValue
    private final int code;
    private final String name;

    @NotNull
    public static RequestCategoryEnum getByCode(@Nullable Integer code) {

        if (code == null) {
            return PC_BROWSER_WINDOWS;
        }

        for (RequestCategoryEnum item : RequestCategoryEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }

        return PC_BROWSER_WINDOWS;

    }

}
