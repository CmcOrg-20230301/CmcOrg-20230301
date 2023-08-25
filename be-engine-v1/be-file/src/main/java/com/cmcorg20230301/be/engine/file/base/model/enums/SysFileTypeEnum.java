package com.cmcorg20230301.be.engine.file.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传：文件类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum SysFileTypeEnum {

    FILE(101), // 文件
    FOLDER(101), // 文件夹

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
