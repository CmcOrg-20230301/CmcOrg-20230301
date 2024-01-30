package com.cmcorg20230301.be.engine.other.app.wx.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信上传文件，媒体文件类型枚举类
 */
@AllArgsConstructor
@Getter
public enum WxMediaUploadTypeEnum {

    IMAGE("image"), // 图片

    VOICE("voice"), // 语音

    VIDEO("video"), // 视频

    THUMB("thumb"), // 缩略图

    FILE("file"), // 普通文件

    ;

    @EnumValue
    @JsonValue
    private final String name; // 名称

}
