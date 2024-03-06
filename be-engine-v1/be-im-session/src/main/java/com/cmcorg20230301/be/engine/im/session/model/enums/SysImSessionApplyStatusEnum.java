package com.cmcorg20230301.be.engine.im.session.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话申请状态，枚举类
 */
@Getter
@AllArgsConstructor
public enum SysImSessionApplyStatusEnum {

    APPLYING(101), // 申请中

    PASSED(201), // 已通过

    REJECTED(301), // 已拒绝

    BLOCKED(401), // 已被拉黑

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

}
