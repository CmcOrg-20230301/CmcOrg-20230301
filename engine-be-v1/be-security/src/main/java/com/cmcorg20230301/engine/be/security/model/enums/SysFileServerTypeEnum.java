package com.cmcorg20230301.engine.be.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存放文件的服务器类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysFileServerTypeEnum {

    MINIO(101, "minio"), //

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码
    private final String name; // 类型名称

}