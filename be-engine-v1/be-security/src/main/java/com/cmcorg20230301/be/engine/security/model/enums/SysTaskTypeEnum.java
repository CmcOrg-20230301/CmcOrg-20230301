package com.cmcorg20230301.be.engine.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysTaskType;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异步任务类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysTaskTypeEnum implements ISysTaskType {

    ACTIVITI_AASR(101), // 流程，音频文件转写任务

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
