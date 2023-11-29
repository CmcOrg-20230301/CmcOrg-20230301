package com.cmcorg20230301.be.engine.security.model.enums;

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
public enum SysRequestCategoryEnum {

    PC_BROWSER_WINDOWS(101, "windows-浏览器"), //
    PC_BROWSER_MAC(102, "mac-浏览器"), //
    PC_BROWSER_LINUX(103, "linux-浏览器"), //

    PC_BROWSER_WINDOWS_WX(104, "windows-浏览器-微信"), //
    PC_BROWSER_MAC_WX(105, "mac-浏览器-微信"), //
    PC_BROWSER_LINUX_WX(106, "linux-浏览器-微信"), //

    PC_CLIENT_WINDOWS(201, "windows-客户端"), //
    PC_CLIENT_MAC(202, "mac-客户端"), //
    PC_CLIENT_LINUX(203, "linux-客户端"), //

    ANDROID(301, "安卓端"), //
    ANDROID_BROWSER(302, "安卓-浏览器"), //
    ANDROID_BROWSER_WX(303, "安卓-浏览器-微信"), //

    IOS(401, "苹果端"), //
    IOS_BROWSER(402, "苹果-浏览器"), //
    IOS_BROWSER_WX(403, "苹果-浏览器-微信"), //

    MINI_PROGRAM_WE_CHAT_ANDROID(501, "小程序-微信-安卓"), //
    MINI_PROGRAM_WE_CHAT_IOS(502, "小程序-微信-苹果"), //

    OFFICIAL_ACCOUNT_WX(601, "公众号-微信"), //

    ;

    @EnumValue
    @JsonValue
    private final int code;
    private final String name;

    @NotNull
    public static SysRequestCategoryEnum getByCode(@Nullable Integer code) {

        if (code == null) {
            return PC_BROWSER_WINDOWS;
        }

        for (SysRequestCategoryEnum item : SysRequestCategoryEnum.values()) {

            if (item.getCode() == code) {

                return item;

            }

        }

        return PC_BROWSER_WINDOWS;

    }

}
