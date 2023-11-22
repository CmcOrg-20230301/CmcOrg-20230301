package com.cmcorg20230301.be.engine.pay.base.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayRefStatus;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关联的状态枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayRefStatusEnum implements ISysPayRefStatus {

    NONE(101), // 无

    WAIT_PAY(201), // 待付款

    FINISHED(301), // 关联业务已处理

    ;

    @EnumValue
    @JsonValue
    private final int code;

}
