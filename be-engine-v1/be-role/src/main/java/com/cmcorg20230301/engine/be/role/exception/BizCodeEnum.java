package com.cmcorg20230301.engine.be.role.exception;

import com.cmcorg20230301.engine.be.model.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizCodeEnum implements IBizCode {

    THE_SAME_ROLE_NAME_EXIST(300011, "操作失败：存在相同的角色名"), //

    ;

    private final int code;
    private final String msg;
}
