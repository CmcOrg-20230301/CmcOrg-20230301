package com.cmcorg20230301.be.engine.file.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存放文件的服务器类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysFileStorageTypeEnum {

    EMPTY(0), // 无，一般用于引用类型的文件

    ALI_YUN(101), // aliyun

    MINIO(201), // minio

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
