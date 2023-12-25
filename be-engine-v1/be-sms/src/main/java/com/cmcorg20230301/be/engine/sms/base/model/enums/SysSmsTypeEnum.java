package com.cmcorg20230301.be.engine.sms.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.sms.base.model.interfaces.ISysSmsType;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信方式类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysSmsTypeEnum implements ISysSmsType {

    ALI_YUN(101), // 阿里云

    TENCENT_YUN(201), // 腾讯云

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
